package cl.ipss.saborgourmet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Datos del formulario "Nueva reserva": mezcla datos del cliente y de la reserva.
 * Se usa un DTO (y no la entidad) para validar lo que escribe el usuario sin exponer el modelo de la base.
 * Las anotaciones @NotBlank, @Email, etc. las valida Spring cuando el controller recibe @Valid.
 */
public class ReservaForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "Maximo 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 80, message = "Maximo 80 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es valido")
    @Size(max = 120, message = "Maximo 120 caracteres")
    private String email;

    @Size(max = 20, message = "Maximo 20 caracteres")
    private String telefono;

    @NotNull(message = "Debes elegir una mesa")
    private Long mesaId;

    /** @DateTimeFormat indica que la fecha llega como texto ISO "2026-11-20" (formato de <input type="date">). */
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede estar en el pasado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime hora;

    @NotNull(message = "Indica la cantidad de personas")
    @Min(value = 1, message = "Minimo 1 persona")
    @Max(value = 30, message = "Maximo 30 personas")
    private Integer cantidadPersonas;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Long getMesaId() { return mesaId; }
    public void setMesaId(Long mesaId) { this.mesaId = mesaId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Integer getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
}
