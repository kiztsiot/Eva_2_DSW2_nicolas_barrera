package cl.ipss.saborgourmet.repository;

import cl.ipss.saborgourmet.entity.Mesa;
import cl.ipss.saborgourmet.enums.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositorio de mesas. */
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    /** Todas las mesas ordenadas por numero (para el panel de administracion). */
    List<Mesa> findAllByOrderByNumeroAsc();

    /** Para validar que no se repita el numero al crear una mesa. */
    boolean existsByNumero(Integer numero);

    /** Para validar que no se repita el numero al editar (se excluye la propia mesa). */
    boolean existsByNumeroAndIdNot(Integer numero, Long id);

    /**
     * Mesas DISPONIBLES para una fecha y hora: activas, con capacidad suficiente y SIN una reserva
     * confirmada en ese mismo momento.
     */
    @Query("""
            SELECT m FROM Mesa m
            WHERE m.activa = true
              AND m.capacidad >= :personas
              AND NOT EXISTS (
                    SELECT r FROM Reserva r
                    WHERE r.mesa = m
                      AND r.fecha = :fecha
                      AND r.hora = :hora
                      AND r.estado = :estado)
            ORDER BY m.capacidad ASC, m.numero ASC
            """)
    List<Mesa> buscarDisponibles(@Param("fecha") LocalDate fecha,
                                 @Param("hora") LocalTime hora,
                                 @Param("personas") int personas,
                                 @Param("estado") EstadoReserva estado);
}
