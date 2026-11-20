package cl.ipss.saborgourmet.enums;

/**
 * Estados posibles de una reserva.
 * Se guarda en la base de datos como texto (VARCHAR) gracias a @Enumerated(EnumType.STRING),
 * y el CHECK de la tabla reserva solo acepta estos mismos valores.
 */
public enum EstadoReserva {
    /** La reserva esta vigente y ocupa la mesa en esa fecha y hora. */
    CONFIRMADA,
    /** La reserva fue cancelada: la mesa queda libre otra vez. */
    CANCELADA
}
