create database festival;

drop database festival;

use festival;

-- 1. Insertar el Rol de Administrador
INSERT INTO user_role (id_rol, rol, created_at, updated_at)
VALUES (1, 'ROLE_ADMIN', NOW(), NOW());

-- 2. Insertar un Festival de prueba (ajustá los campos según las columnas de tu entidad Festival si difieren)
INSERT INTO festival (nombre, temporada, fecha_inicio, fecha_fin, created_at, updated_at)
VALUES ('Festival Primavera 2026', '2020', '2026-11-01',-11-03', NOW(), NOW());

-- 3. Insertar el Usuario Administrador
-- Mail: admin@sistema.com | Contraseña en texto plano: admin
INSERT INTO usuario (id, activo, mail, password, rol_id, created_at, updated_at)
VALUES (1, 1, 'admin@sistema.com', '$2a$10$rAz3VMKuVBfjLW31ejcA0OzE.iBoUZGvwQ3O6pNDw8LRZdPKS9XvC', 1, NOW(), NOW());


select * from usuario;git

select * from user_role;

/* admin@sistema.com pass prueba"*/