package cl.ipss.saborgourmet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos del formulario de administracion para crear o editar una mesa. */
public class MesaForm {

    @NotNull(message = "El numero de mesa es obligatorio")
    @Min(value = 1, message = "El numero debe ser mayor o igual a 1")
    private Integer numero;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad minima es 1")
    @Max(value = 30, message = "La capacidad maxima es 30")
    private Integer capacidad;

    @Size(max = 50, message = "Maximo 50 caracteres")
    private String ubicacion;

    private boolean activa = true;

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
