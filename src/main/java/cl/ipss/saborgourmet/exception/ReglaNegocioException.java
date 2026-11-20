package cl.ipss.saborgourmet.exception;

/**
 * Se lanza cuando se incumple una regla del negocio (mesa ocupada, fecha pasada, capacidad insuficiente...).
 * Los controllers la capturan y muestran el mensaje al usuario dentro del mismo formulario.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
