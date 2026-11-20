package cl.ipss.saborgourmet.service;

import cl.ipss.saborgourmet.dto.ReservaEdicionForm;
import cl.ipss.saborgourmet.dto.ReservaForm;
import cl.ipss.saborgourmet.entity.Mesa;
import cl.ipss.saborgourmet.entity.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaService {

    /** Mesas libres y con capacidad suficiente para esa fecha, hora y cantidad de personas. */
    List<Mesa> buscarMesasDisponibles(LocalDate fecha, LocalTime hora, int personas);

    /** Crea una reserva (y el cliente si no existe) validando todas las reglas. */
    Reserva crear(ReservaForm form);

    /** Busca una reserva por id; lanza RecursoNoEncontradoException si no existe. */
    Reserva obtener(Long id);

    /** Reservas de un cliente, buscadas por su email. */
    List<Reserva> listarPorEmail(String email);

    /** Reservas de todos los clientes; si fecha no es null, solo las de ese dia. */
    List<Reserva> listarTodas(LocalDate fecha);

    /** Cambia fecha, hora o cantidad de personas de una reserva confirmada. */
    Reserva modificar(Long id, ReservaEdicionForm form);

    /** Cancela una reserva confirmada (la mesa queda libre). */
    void cancelar(Long id);
}
