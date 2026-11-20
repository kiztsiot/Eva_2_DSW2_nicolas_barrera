-- =====================================================================
-- V2: datos iniciales para poder probar el sistema de inmediato.
-- =====================================================================

INSERT INTO mesa (numero, capacidad, ubicacion, activa) VALUES
    (1, 2, 'Terraza', TRUE),
    (2, 2, 'Terraza', TRUE),
    (3, 4, 'Salon principal', TRUE),
    (4, 4, 'Salon principal', TRUE),
    (5, 6, 'Salon principal', TRUE),
    (6, 8, 'Salon privado', TRUE);

INSERT INTO cliente (nombre, apellido, email, telefono) VALUES
    ('Camila', 'Rojas', 'camila.rojas@example.com', '+56 9 1111 2222');

-- Una reserva de ejemplo en una fecha lejana (para que no quede en el pasado).
INSERT INTO reserva (cliente_id, mesa_id, fecha, hora, cantidad_personas, estado)
SELECT c.id, m.id, DATE '2030-01-15', TIME '20:00', 2, 'CONFIRMADA'
FROM cliente c, mesa m
WHERE c.email = 'camila.rojas@example.com' AND m.numero = 1;
