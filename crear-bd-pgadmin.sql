-- Para pgAdmin (Query Tool). Antes: crear la base sabor_gourmet_db (clic derecho en Databases > Create > Database...)
-- y abrir el Query Tool SOBRE esa base. Se puede repetir sin errores.

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'sabor_gourmet') THEN
        CREATE USER sabor_gourmet WITH PASSWORD 'sabor_gourmet';
    ELSE
        ALTER USER sabor_gourmet WITH PASSWORD 'sabor_gourmet';
    END IF;
END
$$;

ALTER DATABASE sabor_gourmet_db OWNER TO sabor_gourmet;
GRANT ALL PRIVILEGES ON DATABASE sabor_gourmet_db TO sabor_gourmet;
GRANT ALL ON SCHEMA public TO sabor_gourmet;
