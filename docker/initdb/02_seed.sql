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
  ('Spinning', 'Sesi?n de ciclismo indoor guiada por monitor.'),
  ('Yoga', 'Movilidad, respiraci?n y fuerza con bajo impacto.'),
  ('Cross Training', 'Entrenamiento funcional de alta intensidad.'),
  ('Pilates', 'Trabajo de core, control postural y estabilidad.'),
  ('Zumba', 'Sesion de baile fitness con musica dinamica.'),
  ('HIIT Express', 'Intervalos cortos para maxima quema.'),
  ('Body Pump', 'Entrenamiento con barra y peso moderado.'),
  ('Stretching', 'Movilidad y recuperacion guiada.')
ON CONFLICT DO NOTHING;

-- =========================
-- Salas
-- =========================
INSERT INTO sala (descripcion, aforo, foto)
VALUES
  ('Sala Ciclo Indoor', 25, NULL),
  ('Sala Mind & Body', 20, NULL),
  ('Box Funcional', 18, NULL),
  ('Sala Cardio', 30, NULL),
  ('Sala Fuerza', 22, NULL),
  ('Sala Funcional', 16, NULL)
ON CONFLICT DO NOTHING;

-- =========================
-- Monitores
-- =========================
INSERT INTO monitor (nombre, dni, correo_electronico, contrasena, telefono, ciudad, direccion, codigo_postal)
VALUES
  ('Laura S?nchez', '11111111A', 'laura.sanchez@example.com', 'HASH_PENDIENTE', '600111222', 'Alicante', 'C/ Sol 10', '03001'),
  ('Miguel Torres', '22222222B', 'miguel.torres@example.com', 'HASH_PENDIENTE', '600333444', 'Alicante', 'Av. Mar 5', '03002'),
  ('Sara L?pez',   '33333333C', 'sara.lopez@example.com',   'HASH_PENDIENTE', '600555666', 'Alicante', 'Plaza Centro 1', '03003'),
  ('Javier Molina', '44444444D', 'javier.molina@example.com', 'HASH_PENDIENTE', '600777888', 'Alicante', 'C/ Rio 6', '03008'),
  ('Paula Romero',  '55555555E', 'paula.romero@example.com',  'HASH_PENDIENTE', '600999000', 'Alicante', 'C/ Luna 12', '03009'),
  ('Diego Castillo','66666666F', 'diego.castillo@example.com','HASH_PENDIENTE', '600121212', 'Alicante', 'Av. Sol 7', '03010')
ON CONFLICT DO NOTHING;

-- =========================
-- Socios
-- =========================
-- Nota: contrasena es texto placeholder ("HASH_PENDIENTE").
-- En el backend deberíamos almacenar un hash real (BCrypt, Argon2, etc.).
INSERT INTO socio (
  nombre, correo_electronico, contrasena, telefono,
  id_tarifa, estado, pago_domiciliado, saldo_monedero,
  direccion, ciudad, codigo_postal, clases_gratis
)
SELECT
  v.nombre, v.correo, v.pass, v.tel,
  t.id, v.estado::socio_estado, v.domiciliado, v.saldo,
  v.dir, v.ciudad, v.cp, t.clases_gratis_mes
FROM (VALUES
  ('Juan Perez',  'juan.perez@example.com',  'HASH_PENDIENTE', '611111111', 'Básico',  'ACTIVO',   TRUE,  10.00, 'C/ Norte 2',  'Alicante', '03004'),
  ('Ana Garcia',  'ana.garcia@example.com',  'HASH_PENDIENTE', '622222222', 'Premium', 'ACTIVO',   FALSE, 25.00, 'C/ Sur 8',    'Alicante', '03005'),
  ('Pedro Ruiz',  'pedro.ruiz@example.com',  'HASH_PENDIENTE', '633333333', 'Élite',   'ACTIVO',   TRUE,  50.00, 'Av. Playa 3', 'Alicante', '03006'),
  ('Marta Diaz',  'marta.diaz@example.com',  'HASH_PENDIENTE', '644444444', 'Básico',  'INACTIVO', FALSE,  0.00, 'C/ Lago 9',   'Alicante', '03007'),
  ('Lucia Moreno', 'lucia.moreno@example.com','HASH_PENDIENTE', '655555555', 'Premium', 'ACTIVO',   TRUE,   5.00, 'C/ Centro 4', 'Alicante', '03011'),
  ('Carlos Vega',  'carlos.vega@example.com','HASH_PENDIENTE', '666666666', 'Básico',  'ACTIVO',   FALSE, 12.00, 'C/ Oeste 1',  'Alicante', '03012'),
  ('Elena Ortiz',  'elena.ortiz@example.com','HASH_PENDIENTE', '677777777', 'Premium', 'ACTIVO',   TRUE,  18.00, 'C/ Este 3',   'Alicante', '03013'),
  ('David Gil',    'david.gil@example.com',  'HASH_PENDIENTE', '688888888', 'Élite',   'ACTIVO',   TRUE,  30.00, 'Av. Norte 5', 'Alicante', '03014'),
  ('Sofia Ramos',  'sofia.ramos@example.com','HASH_PENDIENTE', '699999999', 'Básico',  'ACTIVO',   FALSE,  8.00, 'C/ Rio 2',    'Alicante', '03015'),
  ('Mario Navarro','mario.navarro@example.com','HASH_PENDIENTE','600101010','Premium', 'INACTIVO', FALSE,  0.00, 'C/ Mar 9',    'Alicante', '03016')
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
  nombre, hora_ini, hora_fin, precio_extra, fecha, plazas, disponibles,
  id_monitor, id_sala, id_tipo_actividad
)
SELECT
  v.nombre, v.h_ini, v.h_fin, v.precio, v.fecha, v.plazas, v.disponibles,
  m.id, s.id, ta.id
FROM (VALUES
  ('Spinning - Nivel Medio',     '18:00'::time, '19:00'::time, 0.00, (current_date + 1), 25, 25, 'Laura S?nchez',  'Sala Ciclo Indoor', 'Spinning'),
  ('Yoga - Flow Suave',          '19:30'::time, '20:30'::time, 0.00, (current_date + 1), 20, 20, 'Sara L?pez',     'Sala Mind & Body',  'Yoga'),
  ('Cross Training - HIIT',      '20:00'::time, '21:00'::time, 2.50, (current_date + 2), 18, 18, 'Miguel Torres',  'Box Funcional',     'Cross Training'),
  ('Pilates - Core & Stability', '18:30'::time, '19:30'::time, 0.00, (current_date + 2), 20, 20, 'Sara L?pez',     'Sala Mind & Body',  'Pilates'),
  ('Zumba - Ritmo Total',        '19:00'::time, '20:00'::time, 0.00, (current_date + 3), 30, 30, 'Paula Romero',   'Sala Cardio',       'Zumba'),
  ('HIIT Express - 30',          '18:00'::time, '18:30'::time, 1.50, (current_date + 3), 16, 16, 'Diego Castillo', 'Sala Funcional',    'HIIT Express'),
  ('Body Pump - Fuerza',         '20:00'::time, '21:00'::time, 1.00, (current_date + 3), 22, 22, 'Javier Molina',  'Sala Fuerza',       'Body Pump'),
  ('Stretching - Recuperacion',  '21:00'::time, '21:30'::time, 0.00, (current_date + 3), 20, 20, 'Sara L?pez',     'Sala Mind & Body',  'Stretching'),
  ('Spinning - Avanzado',        '07:30'::time, '08:30'::time, 0.00, (current_date + 4), 25, 25, 'Laura S?nchez',  'Sala Ciclo Indoor', 'Spinning'),
  ('Yoga - Sunrise',             '08:00'::time, '09:00'::time, 0.00, (current_date + 4), 20, 20, 'Paula Romero',   'Sala Mind & Body',  'Yoga'),
  ('Cross Training - Power',     '19:00'::time, '20:00'::time, 2.50, (current_date + 4), 18, 18, 'Miguel Torres',  'Box Funcional',     'Cross Training'),
  ('Pilates - Movilidad',        '09:30'::time, '10:30'::time, 0.00, (current_date + 5), 20, 20, 'Sara L?pez',     'Sala Mind & Body',  'Pilates'),
  ('Zumba - Tarde',              '18:30'::time, '19:30'::time, 0.00, (current_date + 5), 30, 30, 'Paula Romero',   'Sala Cardio',       'Zumba'),
  ('HIIT Express - Noon',        '12:30'::time, '13:00'::time, 1.50, (current_date + 5), 16, 16, 'Diego Castillo', 'Sala Funcional',    'HIIT Express'),
  ('Body Pump - Express',        '17:30'::time, '18:15'::time, 1.00, (current_date + 6), 22, 22, 'Javier Molina',  'Sala Fuerza',       'Body Pump'),
  ('Stretching - Relax',         '20:30'::time, '21:00'::time, 0.00, (current_date + 6), 20, 20, 'Laura S?nchez',  'Sala Mind & Body',  'Stretching'),
  ('Spinning - Lunch',           '13:30'::time, '14:30'::time, 0.00, (current_date + 6), 25, 25, 'Miguel Torres',  'Sala Ciclo Indoor', 'Spinning'),
  ('Yoga - Avanzado',            '19:30'::time, '20:30'::time, 0.00, (current_date + 7), 20, 20, 'Paula Romero',   'Sala Mind & Body',  'Yoga'),
  ('Cross Training - AMRAP',     '18:00'::time, '19:00'::time, 2.50, (current_date + 7), 18, 18, 'Diego Castillo', 'Box Funcional',     'Cross Training'),
  ('Pilates - Postura',          '10:00'::time, '11:00'::time, 0.00, (current_date + 7), 20, 20, 'Sara López',     'Sala Mind & Body',  'Pilates'),
  ('Zumba - Weekend',            '11:00'::time, '12:00'::time, 0.00, (current_date + 8), 30, 30, 'Paula Romero',   'Sala Cardio',       'Zumba'),
  ('Body Pump - Weekend',        '12:30'::time, '13:30'::time, 1.00, (current_date + 8), 22, 22, 'Javier Molina',  'Sala Fuerza',       'Body Pump')
) AS v(nombre, h_ini, h_fin, precio, fecha, plazas, disponibles, monitor_nombre, sala_desc, tipo_nombre)
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
  OR
  (so.correo_electronico = 'pedro.ruiz@example.com' AND ac.nombre = 'Cross Training - HIIT')
  OR
  (so.correo_electronico = 'marta.diaz@example.com' AND ac.nombre = 'Pilates - Core & Stability')
  OR
  (so.correo_electronico = 'lucia.moreno@example.com' AND ac.nombre = 'Zumba - Ritmo Total')
  OR
  (so.correo_electronico = 'carlos.vega@example.com' AND ac.nombre = 'Spinning - Avanzado')
  OR
  (so.correo_electronico = 'elena.ortiz@example.com' AND ac.nombre = 'Yoga - Sunrise')
  OR
  (so.correo_electronico = 'david.gil@example.com' AND ac.nombre = 'Body Pump - Fuerza')
  OR
  (so.correo_electronico = 'sofia.ramos@example.com' AND ac.nombre = 'Stretching - Relax')
  OR
  (so.correo_electronico = 'mario.navarro@example.com' AND ac.nombre = 'HIIT Express - 30')
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
  -- total: precio_extra de actividad (o 0). En un sistema real sumar?as m?s conceptos.
  GREATEST(ac.precio_extra, 0),
  'OK'::pago_resultado
FROM reserva r
JOIN socio so ON so.id = r.id_socio
JOIN actividad ac ON ac.id = r.id_actividad
WHERE so.correo_electronico IN (
  'juan.perez@example.com',
  'ana.garcia@example.com',
  'pedro.ruiz@example.com',
  'marta.diaz@example.com',
  'lucia.moreno@example.com',
  'carlos.vega@example.com',
  'elena.ortiz@example.com',
  'david.gil@example.com',
  'sofia.ramos@example.com',
  'mario.navarro@example.com'
)
ON CONFLICT (id_socio, id_actividad) DO NOTHING;

COMMIT;
