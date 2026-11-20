package cl.ipss.saborgourmet.entity;

import cl.ipss.saborgourmet.enums.EstadoReserva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad JPA que representa la tabla "reserva".
 * Relaciona un Cliente con una Mesa en una fecha y hora determinadas.
 *
 * Relaciones:
 *  - Muchas reservas pertenecen a un cliente (@ManyToOne)
 *  - Muchas reservas pertenecen a una mesa (@ManyToOne)
 *
 * Se usa carga EAGER (la de por defecto en @ManyToOne) porque en application.yml
 * open-in-view esta en false: las vistas Thymeleaf leen cliente y mesa despues de cerrada la transaccion,
 * y con carga LAZY eso produciria LazyInitializationException.
 */
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Clave foranea cliente_id. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Clave foranea mesa_id. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "cantidad_personas", nullable = false)
    private Integer cantidadPersonas;

    /** Se guarda el nombre del enum ("CONFIRMADA"/"CANCELADA") y no su posicion numerica. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.CONFIRMADA;

    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;

    public Reserva() {
    }

    /** Antes de insertar por primera vez, registra la fecha y hora de creacion. */
    @PrePersist
    void alCrear() {
        if (creadaEn == null) {
            creadaEn = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Integer getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(Integer cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    /** Atajo para las vistas: indica si la reserva se puede modificar o cancelar. */
    public boolean isConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }
}
