-- =========================================
-- ROL
-- =========================================

INSERT INTO rol (id_rol, rol, usuario_id, created_at, updated_at) VALUES
(1, 'ADMIN',       1, NOW(), NOW()),
(2, 'RESPONSABLE', 2, NOW(), NOW()),
(3, 'EMPLEADO',    3, NOW(), NOW()),
(4, 'EMPLEADO',    4, NOW(), NOW()),
(5, 'EMPLEADO',    5, NOW(), NOW());

-- =========================================
-- USUARIO 
-- =========================================

INSERT INTO usuario (id, username, password, activo, rol_id, created_at, updated_at) VALUES
(1, 'jperez@gmail.com',    '$2a$10$IRKTCM.l2RLdAL7ewu/zU.nifS/LgGnKgYdA7BfYfO51zGiG0fenG', TRUE, 1, NOW(), NOW()),
(2, 'cdiaz@gmail.com',     '$2a$10$IRKTCM.l2RLdAL7ewu/zU.nifS/LgGnKgYdA7BfYfO51zGiG0fenG', TRUE, 2, NOW(), NOW()),
(3, 'pvance@gmail.com',    '$2a$10$IRKTCM.l2RLdAL7ewu/zU.nifS/LgGnKgYdA7BfYfO51zGiG0fenG', TRUE, 3, NOW(), NOW()),
(4, 'tvazquez@gmail.com',  '$2a$10$IRKTCM.l2RLdAL7ewu/zU.nifS/LgGnKgYdA7BfYfO51zGiG0fenG', TRUE, 4, NOW(), NOW()),
(5, 'rgonzalez@gmail.com', '$2a$10$IRKTCM.l2RLdAL7ewu/zU.nifS/LgGnKgYdA7BfYfO51zGiG0fenG', TRUE, 5, NOW(), NOW());

-- =========================================
-- FESTIVAL
-- =========================================
INSERT INTO festival (id, nombre, temporada, fecha_inicio, fecha_fin, created_at, updated_at) VALUES
(1, 'Festival Uno',  'Verano',   '2026-07-20', '2026-07-30', NOW(), NOW()),
(2, 'Festival Dos',  'Invierno', '2026-07-20', '2026-07-30', NOW(), NOW()),
(3, 'Festival Tres', 'Otoño',    '2026-07-20', '2026-07-30', NOW(), NOW());

-- =========================================
-- COSTO
-- =========================================
-- antiguedad_por_anio y plus_*: montos que usa la liquidación de sueldo (valores del enunciado)
INSERT INTO costo (id, costo_montaje, costo_superficie, plus_electricidad, sueldo_base, antiguedad_por_anio, plus_cocinero, plus_cocinero_ayudante, plus_lavaplatos, festival_id, created_at, updated_at) VALUES
(1, 28500,  84000, 61500, 1380000, 5000, 150000, 100000, 50000, 1, NOW(), NOW()),
(2, 36200,  97500, 82300, 1620000, 5000, 150000, 100000, 50000, 2, NOW(), NOW()),
(3, 24800, 112000, 69700, 1475000, 5000, 150000, 100000, 50000, 3, NOW(), NOW());

-- =========================================
-- UNIDAD VENTA
-- =========================================
INSERT INTO unidad_venta (id_unidad_venta, nombre_comercial, superficie, codigo_unico, id_festival, id_usuario) VALUES
(1, 'Pollos',       2000, '1234',  1, 1),
(2, 'Carnes',       3000, '5678',  2, 2),
(3, 'Hamburguesas', 2500, '1111',  1, 4),
(4, 'Pizzas',       3000, '1112',  1, 5),
(5, 'Refrigerios',  4000, '91011', 3, 3);

-- =========================================
-- PUESTO DESARMABLE
-- =========================================
INSERT INTO puesto_desarmable (id_unidad_venta, cantidad, tiempo_montaje) VALUES
(5, 2, 10);

-- =========================================
-- FOOD TRUCK
-- =========================================
INSERT INTO food_truck (id_unidad_venta, patente, usa_luz) VALUES
(1, 'ABC123', TRUE),
(2, 'DEF456', FALSE),
(3, 'FT001',  TRUE),
(4, 'FT002',  FALSE);

-- =========================================
-- STAFF
-- =========================================
INSERT INTO staff (id_staff, dni, nombre, apellido, fecha_nacimiento, fecha_ingreso, sueldo_base, fecha_egreso, unidad_venta_id, usuario_id, created_at, updated_at) VALUES
(1, 11111111, 'Juan',    'Perez',    '1990-05-10', '2024-01-01',  500000, '2024-08-05', 1, 1, NOW(), NOW()),
(2, 22222222, 'Carlos',  'Diaz',     '1992-06-15', '2023-03-02', 1500000, '2025-07-10', 1, 2, NOW(), NOW()),
(3, 33333333, 'Pepe',    'Vance',    '1994-07-20', '2022-05-03', 2500000, '2026-06-15', 5, 3, NOW(), NOW()),
(4, 44444444, 'Tito',    'Vazquez',  '1996-08-25', '2021-07-04', 3500000, '2027-05-20', 1, 4, NOW(), NOW()),
(5, 55555555, 'Roberto', 'Gonzalez', '1998-05-30', '2020-09-05', 3500000, '2028-04-30', 1, 5, NOW(), NOW());

-- =========================================
-- CAJERO
-- =========================================
INSERT INTO cajero (id_staff, turno) VALUES
(1, 'Noche'),
(2, 'Tarde'),
(3, 'Mañana');

-- =========================================
-- COCINERO
-- =========================================
-- plus_categoria guarda la categoría (COCINERO, COCINERO_AYUDANTE o LAVAPLATOS);
-- el monto del plus se toma del costo del festival
INSERT INTO cocinero (id_staff, especialidad, plus_categoria) VALUES
(4, 'Parilla', 'COCINERO'),
(5, 'Pastas',  'COCINERO_AYUDANTE');

-- =========================================
-- PLATO
-- =========================================
INSERT INTO plato (id_plato, nombre, precio, costo, id_unidad_venta) VALUES
(1, 'Hamburguesa', 15000, 14000, 2),
(2, 'Pollo',       15000, 14000, 1);

-- =========================================
-- PEDIDO
-- =========================================
INSERT INTO pedido (id_pedido, fecha, terminado, id_unidad_venta) VALUES
(1, '2026-08-15', FALSE, 1),
(2, '2026-08-20', FALSE, 2),
(3, '2026-08-25', FALSE, 3);

-- =========================================
-- ITEM PEDIDO
-- =========================================
INSERT INTO item_pedido (id_item_pedido, id_pedido, id_plato, cantidad) VALUES
(1, 1, 1, 2),
(2, 2, 2, 3),
(3, 3, 1, 5);