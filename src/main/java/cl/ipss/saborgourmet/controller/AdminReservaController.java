package cl.ipss.saborgourmet.controller;

import cl.ipss.saborgourmet.exception.ReglaNegocioException;
import cl.ipss.saborgourmet.service.ReservaService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Panel de administracion de reservas: el personal ve las reservas (todas o de un dia) y puede cancelarlas. */
@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ReservaService reservaService;

    public AdminReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /** GET /admin/reservas[?fecha=2026-11-20]: sin fecha muestra todas; con fecha, solo las de ese dia. */
    @GetMapping
    public String listar(@RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                         Model model) {
        model.addAttribute("reservas", reservaService.listarTodas(fecha));
        model.addAttribute("fecha", fecha);
        return "admin/reservas";
    }

    /** POST /admin/reservas/{id}/cancelar: cancela y vuelve a la lista. */
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            reservaService.cancelar(id);
            redirect.addFlashAttribute("exito", "La reserva fue cancelada.");
        } catch (ReglaNegocioException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/reservas";
    }
}
