package cl.ipss.saborgourmet.controller;

import cl.ipss.saborgourmet.config.Horarios;
import cl.ipss.saborgourmet.dto.ReservaEdicionForm;
import cl.ipss.saborgourmet.dto.ReservaForm;
import cl.ipss.saborgourmet.entity.Reserva;
import cl.ipss.saborgourmet.exception.ReglaNegocioException;
import cl.ipss.saborgourmet.service.MesaService;
import cl.ipss.saborgourmet.service.ReservaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller de la parte publica: ver disponibilidad, reservar, ver, modificar y cancelar reservas.
 * Flujo: / -> /disponibilidad -> /reservas/nueva -> POST /reservas -> /reservas?email=...
 *
 * Patron usado en los POST: Post/Redirect/Get. Despues de guardar se hace "redirect:" para que,
 * si el usuario recarga la pagina, no se vuelva a enviar el formulario (y no se cree otra reserva).
 */
@Controller
public class ReservaController {

    private final ReservaService reservaService;
    private final MesaService mesaService;

    public ReservaController(ReservaService reservaService, MesaService mesaService) {
        this.reservaService = reservaService;
        this.mesaService = mesaService;
    }

    /** GET /disponibilidad: lista las mesas libres para la fecha, hora y personas indicadas. */
    @GetMapping("/disponibilidad")
    public String disponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam int personas,
            Model model) {
        try {
            model.addAttribute("mesas", reservaService.buscarMesasDisponibles(fecha, hora, personas));
        } catch (ReglaNegocioException ex) {
            // Se vuelve al inicio mostrando el mensaje de error
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("hoy", LocalDate.now());
            model.addAttribute("horas", Horarios.HORAS);
            return "index";
        }
        model.addAttribute("fecha", fecha);
        model.addAttribute("hora", hora);
        model.addAttribute("personas", personas);
        return "disponibilidad";
    }

    /** GET /reservas/nueva: formulario con los datos de la mesa elegida ya cargados. */
    @GetMapping("/reservas/nueva")
    public String nueva(@RequestParam Long mesaId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
                        @RequestParam int personas,
                        Model model) {
        ReservaForm form = new ReservaForm();
        form.setMesaId(mesaId);
        form.setFecha(fecha);
        form.setHora(hora);
        form.setCantidadPersonas(personas);
        model.addAttribute("reservaForm", form);
        model.addAttribute("mesa", mesaService.obtener(mesaId));
        return "reserva-form";
    }

    /**
     * POST /reservas: recibe el formulario. @Valid activa las validaciones del DTO y los errores
     * quedan en BindingResult (debe ir justo despues del objeto validado).
     */
    @PostMapping("/reservas")
    public String crear(@Valid @ModelAttribute("reservaForm") ReservaForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                reservaService.crear(form);
                // Flash attribute: sobrevive a un unico redirect, ideal para mensajes de exito
                redirect.addFlashAttribute("exito", "Tu reserva fue confirmada. Te esperamos.");
                redirect.addAttribute("email", form.getEmail().trim());
                return "redirect:/reservas";
            } catch (ReglaNegocioException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        // Hubo errores: se vuelve a mostrar el formulario con los datos escritos y los mensajes
        if (form.getMesaId() != null) {
            model.addAttribute("mesa", mesaService.obtener(form.getMesaId()));
        }
        return "reserva-form";
    }

    /** GET /reservas: busqueda de reservas por email ("Mis reservas"). */
    @GetMapping("/reservas")
    public String misReservas(@RequestParam(required = false) String email, Model model) {
        if (email != null && !email.isBlank()) {
            model.addAttribute("email", email.trim());
            model.addAttribute("reservas", reservaService.listarPorEmail(email));
        }
        return "mis-reservas";
    }

    /** GET /reservas/{id}/editar: formulario para modificar fecha, hora o personas. */
    @GetMapping("/reservas/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Reserva reserva = reservaService.obtener(id);
        ReservaEdicionForm form = new ReservaEdicionForm();
        form.setFecha(reserva.getFecha());
        form.setHora(reserva.getHora());
        form.setCantidadPersonas(reserva.getCantidadPersonas());
        prepararEdicion(model, reserva, form);
        return "reserva-editar";
    }

    /** POST /reservas/{id}/editar: guarda los cambios. */
    @PostMapping("/reservas/{id}/editar")
    public String modificar(@PathVariable Long id,
                            @Valid @ModelAttribute("form") ReservaEdicionForm form,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirect) {
        Reserva reserva = reservaService.obtener(id);
        if (!result.hasErrors()) {
            try {
                reservaService.modificar(id, form);
                redirect.addFlashAttribute("exito", "La reserva fue modificada.");
                redirect.addAttribute("email", reserva.getCliente().getEmail());
                return "redirect:/reservas";
            } catch (ReglaNegocioException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        prepararEdicion(model, reserva, form);
        return "reserva-editar";
    }

    /** POST /reservas/{id}/cancelar: cancela la reserva y vuelve a "Mis reservas". */
    @PostMapping("/reservas/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        Reserva reserva = reservaService.obtener(id);
        try {
            reservaService.cancelar(id);
            redirect.addFlashAttribute("exito", "La reserva fue cancelada.");
        } catch (ReglaNegocioException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        redirect.addAttribute("email", reserva.getCliente().getEmail());
        return "redirect:/reservas";
    }

    /** Datos que necesita la vista reserva-editar.html, tanto al abrirla como al volver con errores. */
    private void prepararEdicion(Model model, Reserva reserva, ReservaEdicionForm form) {
        model.addAttribute("reserva", reserva);
        model.addAttribute("form", form);
        model.addAttribute("horas", Horarios.HORAS);
        model.addAttribute("hoy", LocalDate.now());
    }
}
