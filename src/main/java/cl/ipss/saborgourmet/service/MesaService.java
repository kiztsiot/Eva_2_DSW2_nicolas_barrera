package cl.ipss.saborgourmet.service;

import cl.ipss.saborgourmet.dto.MesaForm;
import cl.ipss.saborgourmet.entity.Mesa;
import java.util.List;

/** Contrato de la logica de negocio de las mesas (lo usa el panel de administracion). */
public interface MesaService {

    /** Lista todas las mesas ordenadas por numero. */
    List<Mesa> listar();

    /** Busca una mesa por id; lanza RecursoNoEncontradoException si no existe. */
    Mesa obtener(Long id);

    /** Crea una mesa nueva; el numero no puede repetirse. */
    Mesa crear(MesaForm form);

    /** Actualiza numero, capacidad, ubicacion y estado de una mesa existente. */
    Mesa actualizar(Long id, MesaForm form);

    /** Activa la mesa si estaba inactiva, o la desactiva si estaba activa. */
    Mesa cambiarEstado(Long id);
}
