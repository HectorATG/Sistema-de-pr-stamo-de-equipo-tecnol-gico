package mx.utch.prestamos.exception;

import org.springframework.http.HttpStatus;

/** Error de regla de negocio; lleva el código HTTP con el que debe responderse. */
public class NegocioException extends RuntimeException {

    private final HttpStatus status;

    public NegocioException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static NegocioException noEncontrado(String mensaje) {
        return new NegocioException(HttpStatus.NOT_FOUND, mensaje);
    }

    public static NegocioException conflicto(String mensaje) {
        return new NegocioException(HttpStatus.CONFLICT, mensaje);
    }

    public static NegocioException solicitudInvalida(String mensaje) {
        return new NegocioException(HttpStatus.BAD_REQUEST, mensaje);
    }

    public static NegocioException prohibido(String mensaje) {
        return new NegocioException(HttpStatus.FORBIDDEN, mensaje);
    }
}
