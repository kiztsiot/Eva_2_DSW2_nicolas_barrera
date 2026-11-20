package cl.ipss.saborgourmet.service.impl;

import cl.ipss.saborgourmet.dto.ReservaEdicionForm;
import cl.ipss.saborgourmet.dto.ReservaForm;
import cl.ipss.saborgourmet.entity.Cliente;
import cl.ipss.saborgourmet.entity.Mesa;
import cl.ipss.saborgourmet.entity.Reserva;
import cl.ipss.saborgourmet.enums.EstadoReserva;
import cl.ipss.saborgourmet.exception.RecursoNoEncontradoException;
import cl.ipss.saborgourmet.exception.ReglaNegocioException;
import cl.ipss.saborgourmet.repository.ClienteRepository;
import cl.ipss.saborgourmet.repository.MesaRepository;
import cl.ipss.saborgourmet.repository.ReservaRepository;
import cl.ipss.saborgourmet.service.ReservaService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio de las reservas. Aqui viven las reglas:
 *  1. No se puede reservar en el pasado.
 *  2. La mesa debe existir, estar activa y tener capacidad para la cantidad de personas.
 *  3. Una mesa no puede tener dos reservas confirmadas en la misma fecha y hora.
 *  4. Solo se pueden modificar o cancelar reservas confirmadas.
 * El controller no sabe nada de estas reglas: solo llama al service y muestra el resultado.
 */
@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final ClienteRepository clienteRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              MesaRepository mesaRepository,
                              ClienteRepository clienteRepository) {
        this.reservaRepository = reservaRepository;
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mesa> buscarMesasDisponibles(LocalDate fecha, LocalTime hora, int personas) {
        if (personas < 1) {
            throw new ReglaNegocioException("La cantidad de personas debe ser al menos 1.");
        }
        validarNoEsPasado(fecha, hora);
        return mesaRepository.buscarDisponibles(fecha, hora, personas, EstadoReserva.CONFIRMADA);
    }

    @Override
    @Transactional
    public Reserva crear(ReservaForm form) {
        validarNoEsPasado(form.getFecha(), form.getHora());

        Mesa mesa = mesaRepository.findById(form.getMesaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La mesa seleccionada no existe."));
        validarMesa(mesa, form.getCantidadPersonas());

        // Regla 3: la mesa no debe estar ya reservada en ese momento
        if (reservaRepository.existsByMesaIdAndFechaAndHoraAndEstado(
                mesa.getId(), form.getFecha(), form.getHora(), EstadoReserva.CONFIRMADA)) {
            throw new ReglaNegocioException("La mesa " + mesa.getNumero() + " ya esta reservada en ese horario.");
        }

        Reserva reserva = new Reserva();
        reserva.setCliente(obtenerOCrearCliente(form));
        reserva.setMesa(mesa);
        reserva.setFecha(form.getFecha());
        reserva.setHora(form.getHora());
        reserva.setCantidadPersonas(form.getCantidadPersonas());
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        try {
            // saveAndFlush envia el INSERT de inmediato; asi, si otra persona reservo la misma mesa en él
            // mismo instante, el indice unico de la base de datos lo detecta aqui y no mas tarde.
            return reservaRepository.saveAndFlush(reserva);
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("La mesa " + mesa.getNumero() + " acaba de ser reservada por otra persona.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtener(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva solicitada no existe."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarPorEmail(String email) {
        return reservaRepository.findByClienteEmailIgnoreCaseOrderByFechaDescHoraDesc(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarTodas(LocalDate fecha) {
        if (fecha == null) {
            return reservaRepository.findAllByOrderByFechaDescHoraDesc();
        }
        return reservaRepository.findByFechaOrderByHoraAsc(fecha);
    }

    @Override
    @Transactional
    public Reserva modificar(Long id, ReservaEdicionForm form) {
        Reserva reserva = obtener(id);
        if (!reserva.isConfirmada()) {
            throw new ReglaNegocioException("No se puede modificar una reserva cancelada.");
        }
        validarNoEsPasado(form.getFecha(), form.getHora());
        validarMesa(reserva.getMesa(), form.getCantidadPersonas());

        // Se excluye la propia reserva (IdNot) para que no choque consigo misma si solo cambia las personas
        if (reservaRepository.existsByMesaIdAndFechaAndHoraAndEstadoAndIdNot(
                reserva.getMesa().getId(), form.getFecha(), form.getHora(), EstadoReserva.CONFIRMADA, id)) {
            throw new ReglaNegocioException("La mesa " + reserva.getMesa().getNumero()
                    + " ya esta reservada en ese horario.");
        }

        reserva.setFecha(form.getFecha());
        reserva.setHora(form.getHora());
        reserva.setCantidadPersonas(form.getCantidadPersonas());
        try {
            return reservaRepository.saveAndFlush(reserva);
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("La mesa acaba de ser reservada por otra persona en ese horario.");
        }
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        Reserva reserva = obtener(id);
        if (!reserva.isConfirmada()) {
            throw new ReglaNegocioException("La reserva ya estaba cancelada.");
        }
        // No se borra el registro: se marca como CANCELADA para conservar el historial
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    // ------------------------------------------------------------------
    // Metodos auxiliares privados
    // ------------------------------------------------------------------

    /** Regla 1: la combinacion fecha + hora no puede ser anterior al momento actual. */
    private void validarNoEsPasado(LocalDate fecha, LocalTime hora) {
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha y hora elegidas ya pasaron.");
        }
    }

    /** Regla 2: la mesa debe estar activa y alcanzar para todas las personas. */
    private void validarMesa(Mesa mesa, int personas) {
        if (!mesa.isActiva()) {
            throw new ReglaNegocioException("La mesa " + mesa.getNumero() + " no esta disponible.");
        }
        if (mesa.getCapacidad() < personas) {
            throw new ReglaNegocioException("La mesa " + mesa.getNumero() + " solo tiene capacidad para "
                    + mesa.getCapacidad() + " personas.");
        }
    }

    /**
     * Reutiliza el cliente si ya existe uno con ese email (actualizando sus datos);
     * si no existe, crea uno nuevo. El email se guarda en minusculas.
     */
    private Cliente obtenerOCrearCliente(ReservaForm form) {
        String email = form.getEmail().trim().toLowerCase();
        Cliente cliente = clienteRepository.findByEmailIgnoreCase(email).orElseGet(Cliente::new);
        cliente.setEmail(email);
        cliente.setNombre(form.getNombre().trim());
        cliente.setApellido(form.getApellido().trim());
        String telefono = form.getTelefono();
        cliente.setTelefono(telefono == null || telefono.isBlank() ? null : telefono.trim());
        return clienteRepository.save(cliente);
    }
}
