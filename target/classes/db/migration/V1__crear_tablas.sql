-- =====================================================================
-- V1: esquema inicial del sistema de reservas "Sabor Gourmet"
-- Tablas: cliente, mesa, reserva
-- Flyway ejecuta este archivo UNA sola vez y lo registra en flyway_schema_history.
-- IMPORTANTE: no edites este archivo despues de ejecutarlo; los cambios van en V3, V4...
-- =====================================================================

-- ---------------------------------------------------------------------
-- CLIENTE: persona que realiza reservas. El email identifica al cliente.
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre    VARCHAR(80)  NOT NULL,
    apellido  VARCHAR(80)  NOT NULL,
    email     VARCHAR(120) NOT NULL UNIQUE,
    telefono  VARCHAR(20)
);

-- ---------------------------------------------------------------------
-- MESA: mesas fisicas del restaurante. "activa" permite deshabilitar una
-- mesa (por ejemplo, en mantencion) sin borrar su historial de reservas.
-- ---------------------------------------------------------------------
CREATE TABLE mesa (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero     INTEGER     NOT NULL UNIQUE,
    capacidad  INTEGER     NOT NULL CHECK (capacidad > 0),
    ubicacion  VARCHAR(50),
    activa     BOOLEAN     NOT NULL DEFAULT TRUE
);

-- ---------------------------------------------------------------------
-- RESERVA: relaciona un cliente con una mesa en una fecha y hora.
-- Relaciones: cliente 1 --- N reserva   y   mesa 1 --- N reserva
-- ---------------------------------------------------------------------
CREATE TABLE reserva (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id         BIGINT      NOT NULL REFERENCES cliente (id),
    mesa_id            BIGINT      NOT NULL REFERENCES mesa (id),
    fecha              DATE        NOT NULL,
    hora               TIME        NOT NULL,
    cantidad_personas  INTEGER     NOT NULL CHECK (cantidad_personas > 0),
    estado             VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA'
                       CHECK (estado IN ('CONFIRMADA', 'CANCELADA')),
    creada_en          TIMESTAMP   NOT NULL DEFAULT now()
);

-- Una mesa no puede tener dos reservas CONFIRMADAS en la misma fecha y hora.
-- Es un indice unico parcial: las reservas canceladas no cuentan, asi que la
-- mesa queda libre de nuevo al cancelar. Es la ultima defensa contra el doble
-- reservado cuando dos personas reservan "al mismo tiempo".
CREATE UNIQUE INDEX ux_reserva_mesa_fecha_hora
    ON reserva (mesa_id, fecha, hora)
    WHERE estado = 'CONFIRMADA';

-- Indices para las consultas mas frecuentes (buscar por cliente y por fecha).
CREATE INDEX ix_reserva_cliente ON reserva (cliente_id);
CREATE INDEX ix_reserva_fecha   ON reserva (fecha);
