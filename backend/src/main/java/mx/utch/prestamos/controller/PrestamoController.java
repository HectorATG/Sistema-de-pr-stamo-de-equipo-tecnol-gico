package mx.utch.prestamos.controller;

import jakarta.validation.Valid;
import java.util.List;
import mx.utch.prestamos.dto.PrestamoResponse;
import mx.utch.prestamos.dto.SolicitudRequest;
import mx.utch.prestamos.exception.NegocioException;
import mx.utch.prestamos.model.Prestamo;
import mx.utch.prestamos.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints del flujo de préstamo. Los permisos por rol están en SecurityConfig. */
@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponse solicitar(@Valid @RequestBody SolicitudRequest dto, Authentication auth) {
        return PrestamoResponse.de(prestamoService.solicitar(auth.getName(), dto));
    }

    @GetMapping
    public List<PrestamoResponse> listarTodos() {
        return prestamoService.listarTodos().stream().map(PrestamoResponse::de).toList();
    }

    @GetMapping("/mios")
    public List<PrestamoResponse> listarMios(Authentication auth) {
        return prestamoService.listarPorSolicitante(auth.getName()).stream().map(PrestamoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public PrestamoResponse buscar(@PathVariable Long id, Authentication auth) {
        Prestamo prestamo = prestamoService.buscarPorId(id);
        boolean esSolicitante = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SOLICITANTE"));
        if (esSolicitante && !prestamo.getSolicitante().getUsername().equals(auth.getName())) {
            throw NegocioException.prohibido("Solo puedes consultar tus propios préstamos");
        }
        return PrestamoResponse.de(prestamo);
    }

    @PutMapping("/{id}/autorizar")
    public PrestamoResponse autorizar(@PathVariable Long id) {
        return PrestamoResponse.de(prestamoService.autorizar(id));
    }

    @PutMapping("/{id}/rechazar")
    public PrestamoResponse rechazar(@PathVariable Long id) {
        return PrestamoResponse.de(prestamoService.rechazar(id));
    }

    @PutMapping("/{id}/entregar")
    public PrestamoResponse entregar(@PathVariable Long id) {
        return PrestamoResponse.de(prestamoService.entregar(id));
    }

    @PutMapping("/{id}/devolver")
    public PrestamoResponse devolver(@PathVariable Long id) {
        return PrestamoResponse.de(prestamoService.devolver(id));
    }
}
