package cl.ipss.saborgourmet.exception;

/** Se lanza cuando se pide una mesa o reserva que no existe (por ejemplo, un id inventado en la URL). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
