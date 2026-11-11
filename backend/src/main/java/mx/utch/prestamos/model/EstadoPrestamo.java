package mx.utch.prestamos.model;

/** SOLICITADO -> AUTORIZADO -> ENTREGADO -> DEVUELTO; desde SOLICITADO también RECHAZADO. */
public enum EstadoPrestamo {
    SOLICITADO,
    AUTORIZADO,
    RECHAZADO,
    ENTREGADO,
    DEVUELTO
}
