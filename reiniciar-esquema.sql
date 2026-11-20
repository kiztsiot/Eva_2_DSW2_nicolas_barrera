-- Deja la base sabor_gourmet_db completamente vacia para que Flyway vuelva a empezar de cero.
-- Usalo si una migracion fallo a medias o si editaste V1/V2 despues de ejecutarlas.
-- Ejecutar como usuario postgres, con el Query Tool abierto SOBRE sabor_gourmet_db.
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
ALTER SCHEMA public OWNER TO sabor_gourmet;
GRANT ALL ON SCHEMA public TO sabor_gourmet;
