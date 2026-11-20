package cl.ipss.saborgourmet.config;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Horario de atencion del restaurante. Las reservas se hacen por bloque de una hora exacta
 * (12:00, 13:00, ... 22:00). Se usa para llenar el selector de hora de los formularios.
 */
public final class Horarios {

    /** Horas en que se puede reservar: de 12:00 a 22:00. */
    public static final List<LocalTime> HORAS = IntStream.rangeClosed(12, 22)
            .mapToObj(h -> LocalTime.of(h, 0))
            .toList();

    private Horarios() {
        // clase de utilidad: no se instancia
    }
}
