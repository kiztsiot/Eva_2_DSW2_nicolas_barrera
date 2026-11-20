package cl.ipss.saborgourmet.controller;

import cl.ipss.saborgourmet.dto.MesaForm;
import cl.ipss.saborgourmet.entity.Mesa;
import cl.ipss.saborgourmet.exception.ReglaNegocioException;
import cl.ipss.saborgourmet.service.MesaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Panel de administracion de mesas (CRUD): listar, crear, editar y activar/desactivar.
 * Todas las rutas empiezan con /admin/mesas (@RequestMapping a nivel de clase).
 * Nota: el sistema no tiene inicio de sesion (no lo pide el enunciado); en un sistema real
 * estas rutas deberian protegerse con Spring Security.
 */
@Controller
@RequestMapping("/admin/mesas")
public class AdminMesaController {

    private final MesaService mesaService;

    public AdminMesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    /** GET /admin/mesas: tabla con todas las mesas. */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mesas", mesaService.listar());
        return "admin/mesas";
    }

    /** GET /admin/mesas/nueva: formulario vacio. */
    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("form", new MesaForm());
        prepararFormulario(model, "Nueva mesa", "/admin/mesas");
        return "admin/mesa-form";
    }

    /** POST /admin/mesas: crea la mesa. */
    @PostMapping
    public String crear(@Valid @ModelAttribute("form") MesaForm form,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                mesaService.crear(form);
                redirect.addFlashAttribute("exito", "Mesa creada correctamente.");
                return "redirect:/admin/mesas";
            } catch (ReglaNegocioException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        prepararFormulario(model, "Nueva mesa", "/admin/mesas");
        return "admin/mesa-form";
    }

    /** GET /admin/mesas/{id}/editar: formulario cargado con los datos de la mesa. */
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Mesa mesa = mesaService.obtener(id);
        MesaForm form = new MesaForm();
        form.setNumero(mesa.getNumero());
        form.setCapacidad(mesa.getCapacidad());
        form.setUbicacion(mesa.getUbicacion());
        form.setActiva(mesa.isActiva());
        model.addAttribute("form", form);
        prepararFormulario(model, "Editar mesa " + mesa.getNumero(), "/admin/mesas/" + id + "/editar");
        return "admin/mesa-form";
    }

    /** POST /admin/mesas/{id}/editar: guarda los cambios. */
    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("form") MesaForm form,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                mesaService.actualizar(id, form);
                redirect.addFlashAttribute("exito", "Mesa actualizada correctamente.");
                return "redirect:/admin/mesas";
            } catch (ReglaNegocioException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        prepararFormulario(model, "Editar mesa", "/admin/mesas/" + id + "/editar");
        return "admin/mesa-form";
    }

    /** POST /admin/mesas/{id}/estado: activa o desactiva la mesa. */
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        Mesa mesa = mesaService.cambiarEstado(id);
        redirect.addFlashAttribute("exito",
                "La mesa " + mesa.getNumero() + (mesa.isActiva() ? " fue activada." : " fue desactivada."));
        return "redirect:/admin/mesas";
    }

    /** Datos comunes del formulario: titulo de la pagina y a que URL se envia (th:action). */
    private void prepararFormulario(Model model, String titulo, String accion) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
    }
}
