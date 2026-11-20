package cl.ipss.saborgourmet.repository;

import cl.ipss.saborgourmet.entity.Reserva;
import cl.ipss.saborgourmet.enums.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio de reservas. Todos los metodos son consultas derivadas del nombre. */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /** Reservas de un cliente (busqueda por email), de la mas reciente a la mas antigua. */
    List<Reserva> findByClienteEmailIgnoreCaseOrderByFechaDescHoraDesc(String email);

    /** Todas las reservas (panel de administracion). */
    List<Reserva> findAllByOrderByFechaDescHoraDesc();

    /** Reservas de un dia concreto (filtro del panel de administracion). */
    List<Reserva> findByFechaOrderByHoraAsc(LocalDate fecha);

    /** Indica si la mesa ya tiene una reserva en ese estado, fecha y hora (se usa al crear). */
    boolean existsByMesaIdAndFechaAndHoraAndEstado(Long mesaId, LocalDate fecha, LocalTime hora, EstadoReserva estado);

    /** Igual que el anterior pero ignorando una reserva (se usa al modificar, para no chocar consigo misma). */
    boolean existsByMesaIdAndFechaAndHoraAndEstadoAndIdNot(Long mesaId, LocalDate fecha, LocalTime hora,
                                                           EstadoReserva estado, Long id);
}
