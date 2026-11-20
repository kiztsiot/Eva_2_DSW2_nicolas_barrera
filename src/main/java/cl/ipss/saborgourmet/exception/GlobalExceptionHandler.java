package cl.ipss.saborgourmet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Manejo centralizado de errores para todos los controllers (@ControllerAdvice).
 * Asi no hace falta repetir try/catch en cada metodo para los recursos que no existen.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Recurso inexistente: responde 404 y muestra la vista error-negocio.html con el mensaje. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String recursoNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error-negocio";
    }
}
