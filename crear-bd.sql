-- SOLO para psql (usa \gexec y \connect). En pgAdmin usa crear-bd-pgadmin.sql.
-- Ejecutar como superusuario (postgres). Se puede repetir sin errores.
--   psql -U postgres -h localhost -p 5432 -f crear-bd.sql

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'sabor_gourmet') THEN
        CREATE USER sabor_gourmet WITH PASSWORD 'sabor_gourmet';
    ELSE
        ALTER USER sabor_gourmet WITH PASSWORD 'sabor_gourmet';
    END IF;
END
$$;

SELECT 'CREATE DATABASE sabor_gourmet_db OWNER sabor_gourmet'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sabor_gourmet_db')\gexec

\connect sabor_gourmet_db

ALTER DATABASE sabor_gourmet_db OWNER TO sabor_gourmet;
GRANT ALL PRIVILEGES ON DATABASE sabor_gourmet_db TO sabor_gourmet;
GRANT ALL ON SCHEMA public TO sabor_gourmet;
