-- Database: SportBooking

-- DROP DATABASE "SportBooking";

--CREATE DATABASE "SportBooking"
--    WITH
--    OWNER = postgres
--    ENCODING = 'UTF8'
--    LC_COLLATE = 'Spanish_Spain.1252'
--    LC_CTYPE = 'Spanish_Spain.1252'
--    TABLESPACE = pg_default
--    CONNECTION LIMIT = -1;

CREATE TABLE monedero (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT UNIQUE NOT NULL REFERENCES user(id) ON DELETE CASCADE,
  saldo NUMERIC(12,2) DEFAULT 0
);

CREATE TABLE categoria (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT
);

CREATE TABLE pista (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(120),
  descripcion TEXT,
  capacidad INT,
  precio_hora NUMERIC(10,2),
  estado VARCHAR(20) DEFAULT 'ACTIVA',
  categoria_id BIGINT REFERENCES categoria(id)
);

CREATE TABLE horario (
  id BIGSERIAL PRIMARY KEY,
  fecha DATE,                  -- usa NULL si empleas plantilla recurrente
  dia_semana SMALLINT,         -- 0..6 si empleas plantilla
  inicio TIME,
  fin TIME,
  vigente_desde DATE,
  vigente_hasta DATE,
  CHECK (fin > inicio)
);

-- N–N Pista–Horario
CREATE TABLE pista_horario (
  id BIGSERIAL PRIMARY KEY,
  pista_id BIGINT NOT NULL REFERENCES pista(id) ON DELETE CASCADE,
  horario_id BIGINT NOT NULL REFERENCES horario(id) ON DELETE CASCADE,
  bloqueada BOOLEAN DEFAULT FALSE,
  precio_override NUMERIC(10,2),
  UNIQUE (pista_id, horario_id)
);

-- Reserva (User reserva una Pista en un Horario)
CREATE TABLE reserva (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES user(id) ON DELETE CASCADE,
  pista_id BIGINT NOT NULL REFERENCES pista(id) ON DELETE CASCADE,
  horario_id BIGINT NOT NULL REFERENCES horario(id) ON DELETE CASCADE,
  estado VARCHAR(20) DEFAULT 'PENDIENTE',
  importe NUMERIC(10,2),
  UNIQUE (pista_id, horario_id)  -- evita doble booking del mismo slot
  -- Si quieres exigir que el slot exista en la cuadrícula:
  -- , FOREIGN KEY (pista_id, horario_id) REFERENCES pista_horario(pista_id, horario_id)
);