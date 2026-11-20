# Sabor Gourmet - sistema de reservas

Evaluacion Unidad 2 - Desarrollo de software web II (IPSS).
Spring Boot 3.5 / Java 21 + Spring MVC + Spring Data JPA + Thymeleaf + PostgreSQL 18 + Flyway.

## Funcionalidades

**Clientes** (rutas publicas)
- `/` buscar disponibilidad por fecha, hora y cantidad de personas
- `/disponibilidad` ver las mesas libres
- `/reservas/nueva` reservar (crea el cliente por email si no existe)
- `/reservas?email=...` ver mis reservas, modificarlas o cancelarlas

**Personal del restaurante** (panel admin, sin login porque el enunciado no lo pide)
- `/admin/mesas` crear, editar y activar/desactivar mesas
- `/admin/reservas` ver todas las reservas, filtrar por fecha y cancelar

## Reglas de negocio (ReservaServiceImpl)
1. No se puede reservar en el pasado.
2. La mesa debe estar activa y tener capacidad para las personas.
3. Una mesa no puede tener dos reservas confirmadas en la misma fecha y hora
   (validado en el service y respaldado por un indice unico parcial en la base).
4. Solo se pueden modificar o cancelar reservas confirmadas. Cancelar no borra: marca CANCELADA.
Las reservas son por bloque de una hora exacta (12:00 a 22:00, ver `config/Horarios.java`).

## Estructura

```
cl.ipss.saborgourmet
├── SaborGourmetApplication
├── config/       Horarios (horas reservables)
├── controller/   InicioController, ReservaController, AdminMesaController, AdminReservaController
├── dto/          ReservaForm, ReservaEdicionForm, MesaForm (formularios + validaciones)
├── entity/       Cliente, Mesa, Reserva
├── enums/        EstadoReserva
├── exception/    ReglaNegocioException, RecursoNoEncontradoException, GlobalExceptionHandler
├── repository/   ClienteRepository, MesaRepository, ReservaRepository
└── service/      MesaService, ReservaService  (+ impl/)
src/main/resources
├── db/migration/ V1__crear_tablas.sql, V2__datos_iniciales.sql
├── templates/    fragments, index, disponibilidad, reserva-form, mis-reservas, reserva-editar, error-negocio, admin/*
└── static/       style.css (responsivo)
```

## Base de datos (PostgreSQL 18)

1. Crea el usuario y la base (una sola vez): `crear-bd.sql` (psql) o `crear-bd-pgadmin.sql` (pgAdmin).
2. Las tablas las crea Flyway al arrancar la app (`V1`) y tambien carga 6 mesas de ejemplo (`V2`).
3. Si algo falla a medias o editaste una migracion ya ejecutada: ejecuta `reiniciar-esquema.sql`
   (como postgres, sobre `sabor_gourmet_db`) y vuelve a arrancar.

Tablas: `cliente` 1—N `reserva` N—1 `mesa`.

## Ejecutar

Necesitas Java 21.

```powershell
.\mvnw.cmd spring-boot:run
```

Abre http://localhost:8081. Variables opcionales: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`.

## Recordatorio de la entrega
- Informe Word/PDF: portada, indice, introduccion, desarrollo (explicacion del codigo por archivo), conclusion y bibliografia.
- Arial 12, interlineado 1,15, justificado, paginas numeradas.
- Nombre del archivo: `EVU2_GRUPO_APELLIDO_NOMBRE`.
