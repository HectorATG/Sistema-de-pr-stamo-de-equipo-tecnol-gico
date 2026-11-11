package mx.utch.prestamos.dto;

import java.time.LocalDate;
import mx.utch.prestamos.model.EstadoEquipo;
import mx.utch.prestamos.model.EstadoPrestamo;
import mx.utch.prestamos.model.Prestamo;
import mx.utch.prestamos.model.TipoEquipo;

/** Vista de un préstamo que se devuelve como JSON. */
public record PrestamoResponse(
        Long id,
        String solicitante,
        String solicitanteUsername,
        Long equipoId,
        String equipo,
        TipoEquipo tipoEquipo,
        EstadoEquipo estadoEquipo,
        LocalDate fechaSolicitud,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaEntrega,
        LocalDate fechaDevolucion,
        EstadoPrestamo estado) {

    public static PrestamoResponse de(Prestamo p) {
        return new PrestamoResponse(
                p.getId(),
                p.getSolicitante().getNombre(),
                p.getSolicitante().getUsername(),
                p.getEquipo().getId(),
                p.getEquipo().getNombre(),
                p.getEquipo().getTipo(),
                p.getEquipo().getEstado(),
                p.getFechaSolicitud(),
                p.getFechaInicio(),
                p.getFechaFin(),
                p.getFechaEntrega(),
                p.getFechaDevolucion(),
                p.getEstado());
    }
}
