package cl.ipss.saborgourmet.service.impl;

import cl.ipss.saborgourmet.dto.MesaForm;
import cl.ipss.saborgourmet.entity.Mesa;
import cl.ipss.saborgourmet.exception.RecursoNoEncontradoException;
import cl.ipss.saborgourmet.exception.ReglaNegocioException;
import cl.ipss.saborgourmet.repository.MesaRepository;
import cl.ipss.saborgourmet.service.MesaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion de MesaService. @Service la registra como componente de Spring y
 * @Transactional hace que cada metodo corra dentro de una transaccion de base de datos.
 */
@Service
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;

    /** Inyeccion por constructor: Spring entrega el repositorio ya creado. */
    public MesaServiceImpl(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mesa> listar() {
        return mesaRepository.findAllByOrderByNumeroAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Mesa obtener(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La mesa solicitada no existe."));
    }

    @Override
    @Transactional
    public Mesa crear(MesaForm form) {
        if (mesaRepository.existsByNumero(form.getNumero())) {
            throw new ReglaNegocioException("Ya existe una mesa con el numero " + form.getNumero() + ".");
        }
        Mesa mesa = new Mesa();
        copiarDatos(form, mesa);
        return mesaRepository.save(mesa);
    }

    @Override
    @Transactional
    public Mesa actualizar(Long id, MesaForm form) {
        Mesa mesa = obtener(id);
        // El numero puede repetirse solo con la propia mesa (por eso se excluye su id)
        if (mesaRepository.existsByNumeroAndIdNot(form.getNumero(), id)) {
            throw new ReglaNegocioException("Ya existe otra mesa con el numero " + form.getNumero() + ".");
        }
        copiarDatos(form, mesa);
        return mesaRepository.save(mesa);
    }

    @Override
    @Transactional
    public Mesa cambiarEstado(Long id) {
        Mesa mesa = obtener(id);
        mesa.setActiva(!mesa.isActiva());
        return mesaRepository.save(mesa);
    }

    /** Pasa los datos del formulario a la entidad. */
    private void copiarDatos(MesaForm form, Mesa mesa) {
        mesa.setNumero(form.getNumero());
        mesa.setCapacidad(form.getCapacidad());
        String ubicacion = form.getUbicacion();
        mesa.setUbicacion(ubicacion == null || ubicacion.isBlank() ? null : ubicacion.trim());
        mesa.setActiva(form.isActiva());
    }
}
