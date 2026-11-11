package mx.utch.prestamos.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Autenticación HTTP Basic y autorización por rol (RNF1).
 * 401 = sin credenciales o incorrectas; 403 = rol sin permiso.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String SOLICITANTE = "SOLICITANTE";
    private static final String RESPONSABLE = "RESPONSABLE";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(antMatcher("/error")).permitAll()
                        // Solicitante
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/prestamos")).hasRole(SOLICITANTE)
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/prestamos/mios")).hasRole(SOLICITANTE)
                        // Responsable
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/prestamos")).hasRole(RESPONSABLE)
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/prestamos/*/autorizar")).hasRole(RESPONSABLE)
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/prestamos/*/rechazar")).hasRole(RESPONSABLE)
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/prestamos/*/entregar")).hasRole(RESPONSABLE)
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/prestamos/*/devolver")).hasRole(RESPONSABLE)
                        // Todo lo demás: cualquier usuario autenticado
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(noAutenticado()))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(noAutenticado())
                        .accessDeniedHandler(sinPermiso()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Permite que el frontend Angular (localhost:4200) llame a la API. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** 401 en JSON y sin la cabecera WWW-Authenticate, para que el navegador no abra su propio diálogo. */
    private AuthenticationEntryPoint noAutenticado() {
        return (request, response, ex) -> escribir(response, HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized", "Credenciales ausentes o incorrectas");
    }

    private AccessDeniedHandler sinPermiso() {
        return (request, response, ex) -> escribir(response, HttpServletResponse.SC_FORBIDDEN,
                "Forbidden", "Tu rol no tiene permiso para esta operación");
    }

    private void escribir(HttpServletResponse response, int status, String error, String mensaje)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"status\":" + status + ",\"error\":\"" + error + "\",\"mensaje\":\"" + mensaje + "\"}");
    }
}
