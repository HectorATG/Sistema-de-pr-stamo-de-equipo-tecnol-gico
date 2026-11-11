package mx.utch.prestamos.controller;

import mx.utch.prestamos.dto.UsuarioResponse;
import mx.utch.prestamos.exception.NegocioException;
import mx.utch.prestamos.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Inicio de sesión: si las credenciales HTTP Basic son válidas, devuelve el usuario y su rol. */
    @GetMapping("/me")
    public UsuarioResponse me(Authentication auth) {
        return usuarioRepository.findByUsername(auth.getName())
                .map(UsuarioResponse::de)
                .orElseThrow(() -> NegocioException.noEncontrado("Usuario no encontrado"));
    }
}
