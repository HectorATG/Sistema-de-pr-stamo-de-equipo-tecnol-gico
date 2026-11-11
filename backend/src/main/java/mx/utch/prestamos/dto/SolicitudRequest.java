package mx.utch.prestamos.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Cuerpo de POST /api/prestamos. */
public record SolicitudRequest(
        @NotNull(message = "equipoId es obligatorio") Long equipoId,
        @NotNull(message = "fechaInicio es obligatoria") LocalDate fechaInicio,
        @NotNull(message = "fechaFin es obligatoria") LocalDate fechaFin) {
}
