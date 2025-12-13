-- Datos iniciales FitGym

BEGIN;

-- =========================
-- Tarifa (3 obligatorias)
-- =========================
INSERT INTO tarifa (nombre, cuota, descripcion, clases_gratis_mes)
VALUES
  ('Básico', 29.99, 'Acceso general al gimnasio. Incluye un número limitado de clases gratuitas al mes.', 2),
  ('Premium', 49.99, 'Incluye más clases gratuitas y prioridad en reservas.', 6),
  ('Élite', 79.99, 'Acceso completo, clases ilimitadas y ventajas adicionales.', 999)
ON CONFLICT DO NOTHING;

-- =========================
-- Tipos de actividad
-- =========================
INSERT INTO tipo_actividad (nombre, descripcion)
VALUES
  ('Spinning', 'Sesión de ciclismo indoor guiada por monitor.'),
  ('Yoga', 'Movilidad, respiración y fuerza con bajo impacto.'),
  ('Cross Training', 'Entrenamiento funcional de alta intensidad.'),
  ('Pilates', 'Trabajo de core, control postural y estabilidad.')
ON CONFLICT DO NOTHING;

-- =========================
-- Salas
-- =========================
INSERT INTO sala (descripcion, aforo, foto)
VALUES
  ('Sala Ciclo Indoor', 25, NULL),
  ('Sala Mind & Body', 20, NULL),
  ('Box Funcional', 18, NULL)
ON CONFLICT DO NOTHING;

-- =========================
-- Monitores
-- =========================
INSERT INTO monitor (nombre, dni, correo_electronico, contrasena, telefono, ciudad, direccion, codigo_postal)
VALUES
  ('Laura Sánchez', '11111111A', 'laura.sanchez@example.com', 'HASH_PENDIENTE', '600111222', 'Alicante', 'C/ Sol 10', '03001'),
  ('Miguel Torres', '22222222B', 'miguel.torres@example.com', 'HASH_PENDIENTE', '600333444', 'Alicante', 'Av. Mar 5', '03002'),
  ('Sara López',   '33333333C', 'sara.lopez@example.com',   'HASH_PENDIENTE', '600555666', 'Alicante', 'Plaza Centro 1', '03003')
ON CONFLICT DO NOTHING;

-- =========================
-- Socios
-- =========================
-- Nota: contrasena es texto placeholder ("HASH_PENDIENTE").
-- En el backend deberíamos almacenar un hash real (BCrypt, Argon2, etc.).
INSERT INTO socio (
  nombre, correo_electronico, contrasena, telefono,
  id_tarifa, estado, pago_domiciliado, saldo_monedero,
  direccion, ciudad, codigo_postal
)
SELECT
  v.nombre, v.correo, v.pass, v.tel,
  t.id, v.estado::socio_estado, v.domiciliado, v.saldo,
  v.dir, v.ciudad, v.cp
FROM (VALUES
  ('Juan Pérez',  'juan.perez@example.com',  'HASH_PENDIENTE', '611111111', 'Básico',  'ACTIVO',   TRUE,  10.00, 'C/ Norte 2',  'Alicante', '03004'),
  ('Ana García',  'ana.garcia@example.com',  'HASH_PENDIENTE', '622222222', 'Premium', 'ACTIVO',   FALSE, 25.00, 'C/ Sur 8',    'Alicante', '03005'),
  ('Pedro Ruiz',  'pedro.ruiz@example.com',  'HASH_PENDIENTE', '633333333', 'Élite',   'ACTIVO',   TRUE,  50.00, 'Av. Playa 3', 'Alicante', '03006'),
  ('Marta Díaz',  'marta.diaz@example.com',  'HASH_PENDIENTE', '644444444', 'Básico',  'INACTIVO', FALSE,  0.00, 'C/ Lago 9',   'Alicante', '03007')
) AS v(nombre, correo, pass, tel, tarifa_nombre, estado, domiciliado, saldo, dir, ciudad, cp)
JOIN tarifa t ON t.nombre = v.tarifa_nombre
ON CONFLICT (correo_electronico) DO NOTHING;

-- =========================
-- Actividades
-- =========================
-- Creamos actividades futuras y asignamos monitor/sala/tipo por nombre.
WITH
m AS (SELECT id, nombre FROM monitor),
s AS (SELECT id, descripcion FROM sala),
ta AS (SELECT id, nombre FROM tipo_actividad)
INSERT INTO actividad (
  nombre, hora_ini, hora_fin, precio_extra, fecha, plazas,
  id_monitor, id_sala, id_tipo_actividad
)
SELECT
  v.nombre, v.h_ini, v.h_fin, v.precio, v.fecha, v.plazas,
  m.id, s.id, ta.id
FROM (VALUES
  ('Spinning - Nivel Medio',     '18:00'::time, '19:00'::time, 0.00, (current_date + 1), 25, 'Laura Sánchez',  'Sala Ciclo Indoor', 'Spinning'),
  ('Yoga - Flow Suave',          '19:30'::time, '20:30'::time, 0.00, (current_date + 1), 20, 'Sara López',     'Sala Mind & Body',  'Yoga'),
  ('Cross Training - HIIT',      '20:00'::time, '21:00'::time, 2.50, (current_date + 2), 18, 'Miguel Torres',  'Box Funcional',     'Cross Training'),
  ('Pilates - Core & Stability', '18:30'::time, '19:30'::time, 0.00, (current_date + 2), 20, 'Sara López',     'Sala Mind & Body',  'Pilates')
) AS v(nombre, h_ini, h_fin, precio, fecha, plazas, monitor_nombre, sala_desc, tipo_nombre)
JOIN m  ON m.nombre = v.monitor_nombre
JOIN s  ON s.descripcion = v.sala_desc
JOIN ta ON ta.nombre = v.tipo_nombre
ON CONFLICT DO NOTHING;

-- =========================
-- Reservas (PK compuesta)
-- =========================
-- 2 reservas de ejemplo: Juan reserva Spinning, Ana reserva Yoga (porque quiere mantenerse flexible)
INSERT INTO reserva (id_socio, id_actividad, fecha_alta, estado)
SELECT
  so.id,
  ac.id,
  now(),
  'CONFIRMADA'::reserva_estado
FROM socio so
JOIN actividad ac ON (
  (so.correo_electronico = 'juan.perez@example.com' AND ac.nombre = 'Spinning - Nivel Medio')
  OR
  (so.correo_electronico = 'ana.garcia@example.com' AND ac.nombre = 'Yoga - Flow Suave')
)
ON CONFLICT (id_socio, id_actividad) DO NOTHING;

-- =========================
-- Pagos (1:1 con Reserva)
-- =========================
-- Pago asociado a la reserva de Juan (OK) y a la de Ana (OK)
INSERT INTO pago (nombre, id_socio, id_actividad, fecha_pago, cantidad, resultado_pago)
SELECT
  'Pago reserva actividad',
  r.id_socio,
  r.id_actividad,
  now(),
  -- total: precio_extra de actividad (o 0). En un sistema real sumarías más conceptos.
  GREATEST(ac.precio_extra, 0),
  'OK'::pago_resultado
FROM reserva r
JOIN socio so ON so.id = r.id_socio
JOIN actividad ac ON ac.id = r.id_actividad
WHERE so.correo_electronico IN ('juan.perez@example.com', 'ana.garcia@example.com')
ON CONFLICT (id_socio, id_actividad) DO NOTHING;

COMMIT;
