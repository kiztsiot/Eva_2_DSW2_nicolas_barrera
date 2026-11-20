package cl.ipss.saborgourmet.controller;

import cl.ipss.saborgourmet.config.Horarios;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Pagina de inicio: formulario para consultar la disponibilidad de mesas. */
@Controller
public class InicioController {

    /**
     * GET / -> muestra index.html (src/main/resources/templates/index.html).
     * El Model es el "maletin" con los datos que Thymeleaf usa para dibujar la pagina.
     */
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("horas", Horarios.HORAS);
        return "index";
    }
}
