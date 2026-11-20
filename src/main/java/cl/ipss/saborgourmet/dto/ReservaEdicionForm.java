package cl.ipss.saborgourmet.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Datos del formulario "Modificar reserva". Solo se pueden cambiar la fecha, la hora y la cantidad de personas;
 * la mesa y el cliente se mantienen (si se necesita otra mesa, se cancela y se reserva de nuevo).
 */
public class ReservaEdicionForm {

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

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Integer getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
}
