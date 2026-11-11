package mx.utch.prestamos.controller;

import java.util.List;
import mx.utch.prestamos.model.Equipo;
import mx.utch.prestamos.model.EstadoEquipo;
import mx.utch.prestamos.service.EquipoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    /** GET /api/equipos?estado=DISPONIBLE — sin el parámetro devuelve todos. */
    @GetMapping
    public List<Equipo> listar(@RequestParam(required = false) EstadoEquipo estado) {
        return estado == EstadoEquipo.DISPONIBLE ? equipoService.listarDisponibles() : equipoService.listar(estado);
    }
}
