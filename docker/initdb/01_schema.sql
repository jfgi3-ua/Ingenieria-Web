-- Esquema inicial FitGym (PostgreSQL)
-- Nota: Este script está pensado para ejecutarse en un initdb (primer arranque con data dir vacío).
-- Si se ejecuta sobre una BD ya existente, puede fallar si las tablas ya existen. Se recomienda usar migraciones en ese caso.
-- Para eliminar la base de datos y volver a crearla desde cero, eliminar el volumen Docker asociado (fitgym_data).

BEGIN;

-- =========================
-- Tipos controlados (ENUM)
-- =========================
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'pago_resultado') THEN
    CREATE TYPE pago_resultado AS ENUM ('OK', 'FAIL');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tpvv_pago_estado') THEN
    CREATE TYPE tpvv_pago_estado AS ENUM ('PENDING', 'COMPLETED', 'FAILED');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'socio_estado') THEN
    CREATE TYPE socio_estado AS ENUM ('ACTIVO', 'INACTIVO');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'reserva_estado') THEN
    CREATE TYPE reserva_estado AS ENUM ('PENDIENTE', 'CONFIRMADA', 'CANCELADA');
  END IF;
END $$;

-- =========================
-- Tablas maestras
-- =========================

CREATE TABLE IF NOT EXISTS tarifa (
  id               BIGSERIAL PRIMARY KEY,
  nombre           VARCHAR(80)  NOT NULL,
  cuota            NUMERIC(10,2) NOT NULL CHECK (cuota >= 0),
  descripcion      TEXT,
  clases_gratis_mes INTEGER NOT NULL DEFAULT 0 CHECK (clases_gratis_mes >= 0)
);

CREATE TABLE IF NOT EXISTS monitor (
  id                BIGSERIAL PRIMARY KEY,
  nombre            VARCHAR(80)  NOT NULL,
  dni               VARCHAR(20)  NOT NULL,
  correo_electronico VARCHAR(120) NOT NULL,
  contrasena        VARCHAR(255) NOT NULL,
  telefono          VARCHAR(20),
  ciudad            VARCHAR(80),
  direccion         VARCHAR(200),
  codigo_postal     VARCHAR(10),
  CONSTRAINT uq_monitor_dni UNIQUE (dni),
  CONSTRAINT uq_monitor_email UNIQUE (correo_electronico)
);

CREATE TABLE IF NOT EXISTS sala (
  id          BIGSERIAL PRIMARY KEY,
  descripcion TEXT,
  aforo       INTEGER NOT NULL CHECK (aforo >= 0),
  foto        TEXT
);

CREATE TABLE IF NOT EXISTS tipo_actividad (
  id          BIGSERIAL PRIMARY KEY,
  nombre      VARCHAR(80) NOT NULL,
  descripcion TEXT
);

-- =========================
-- Socio
-- =========================

CREATE TABLE IF NOT EXISTS socio (
  id                 BIGSERIAL PRIMARY KEY,
  nombre             VARCHAR(80)  NOT NULL,
  correo_electronico VARCHAR(120) NOT NULL,
  contrasena         VARCHAR(255) NOT NULL,
  telefono           VARCHAR(20),

  id_tarifa          BIGINT NOT NULL,
  estado             socio_estado NOT NULL DEFAULT 'ACTIVO',
  pago_domiciliado   BOOLEAN NOT NULL DEFAULT FALSE,
  saldo_monedero     NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (saldo_monedero >= 0),

  direccion          VARCHAR(200),
  ciudad             VARCHAR(80),
  codigo_postal      VARCHAR(10),
  clases_gratis      INTEGER NOT NULL DEFAULT 0 CHECK (clases_gratis >= 0),
  token_registro     VARCHAR(120),

  CONSTRAINT uq_socio_email UNIQUE (correo_electronico),
  CONSTRAINT fk_socio_tarifa
    FOREIGN KEY (id_tarifa) REFERENCES tarifa(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_socio_id_tarifa ON socio(id_tarifa);

-- =========================
-- Actividad
-- =========================

CREATE TABLE IF NOT EXISTS actividad (
  id               BIGSERIAL PRIMARY KEY,
  nombre           VARCHAR(120) NOT NULL,

  hora_ini         TIME NOT NULL,
  hora_fin         TIME NOT NULL,
  precio_extra     NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (precio_extra >= 0),
  fecha            DATE NOT NULL,
  plazas           INTEGER NOT NULL CHECK (plazas >= 0),
  disponibles      INTEGER NOT NULL CHECK (disponibles <= plazas),    

  id_monitor       BIGINT NOT NULL,
  id_sala          BIGINT NOT NULL,
  id_tipo_actividad BIGINT NOT NULL,

  CONSTRAINT ck_actividad_horas CHECK (hora_fin > hora_ini),

  CONSTRAINT fk_actividad_monitor
    FOREIGN KEY (id_monitor) REFERENCES monitor(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

  CONSTRAINT fk_actividad_sala
    FOREIGN KEY (id_sala) REFERENCES sala(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

  CONSTRAINT fk_actividad_tipo
    FOREIGN KEY (id_tipo_actividad) REFERENCES tipo_actividad(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_actividad_id_monitor ON actividad(id_monitor);
CREATE INDEX IF NOT EXISTS idx_actividad_id_sala ON actividad(id_sala);
CREATE INDEX IF NOT EXISTS idx_actividad_id_tipo ON actividad(id_tipo_actividad);
CREATE INDEX IF NOT EXISTS idx_actividad_fecha ON actividad(fecha);

-- =========================
-- Reserva (PK compuesta)
-- =========================

CREATE TABLE IF NOT EXISTS reserva (
  id_socio     BIGINT NOT NULL,
  id_actividad BIGINT NOT NULL,
  fecha_alta   TIMESTAMPTZ NOT NULL DEFAULT now(),
  estado       VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

  CONSTRAINT pk_reserva PRIMARY KEY (id_socio, id_actividad),

  CONSTRAINT fk_reserva_socio
    FOREIGN KEY (id_socio) REFERENCES socio(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

  CONSTRAINT fk_reserva_actividad
    FOREIGN KEY (id_actividad) REFERENCES actividad(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Para consultas por actividad (la PK indexa por id_socio primero, esto optimiza el acceso por id_actividad).
CREATE INDEX IF NOT EXISTS idx_reserva_id_actividad ON reserva(id_actividad);

-- =========================
-- Pago (1:1 con Reserva)
-- =========================
-- Dado que Reserva tiene PK compuesta (id_socio, id_actividad),
-- Pago referencia esa misma PK compuesta y la marcamos UNIQUE para forzar 1:1.

CREATE TABLE IF NOT EXISTS pago (
  id              BIGSERIAL PRIMARY KEY,
  nombre          VARCHAR(120) NOT NULL,

  id_socio        BIGINT NOT NULL,
  id_actividad    BIGINT NOT NULL,

  fecha_pago      TIMESTAMPTZ NOT NULL DEFAULT now(),
  cantidad        NUMERIC(10,2) NOT NULL CHECK (cantidad >= 0),
  resultado_pago  pago_resultado NOT NULL,

  CONSTRAINT uq_pago_reserva UNIQUE (id_socio, id_actividad),

  CONSTRAINT fk_pago_reserva
    FOREIGN KEY (id_socio, id_actividad)
    REFERENCES reserva(id_socio, id_actividad)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_pago_reserva ON pago(id_socio, id_actividad);

-- =========================
-- Pago Registro (TPVV)
-- =========================

CREATE TABLE IF NOT EXISTS pago_registro (
  id                BIGSERIAL PRIMARY KEY,
  token             VARCHAR(120) NOT NULL UNIQUE,
  estado            tpvv_pago_estado NOT NULL DEFAULT 'PENDING',
  importe           NUMERIC(10,2) NOT NULL CHECK (importe >= 0),
  external_reference VARCHAR(120),
  callback_url      TEXT NOT NULL,
  payment_url       TEXT,
  provider_status   VARCHAR(40),
  failure_reason    TEXT,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  completed_at      TIMESTAMPTZ,
  failed_at         TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_pago_registro_estado ON pago_registro(estado);

COMMIT;
