DROP DATABASE IF EXISTS cafeteria_instituto2;
CREATE DATABASE cafeteria_instituto2;
USE cafeteria_instituto2;

CREATE TABLE usuario (
    email VARCHAR(150) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
	rol ENUM('PROFESOR', 'COCINERO', 'ADMIN') NOT NULL,
    fecha_registro DATE NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE encuesta (
    id_encuesta INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_creacion DATE NOT NULL
);

CREATE TABLE pregunta (
    id_pregunta INT AUTO_INCREMENT PRIMARY KEY,
    texto TEXT NOT NULL,
    id_encuesta INT NOT NULL,
    FOREIGN KEY (id_encuesta) REFERENCES encuesta(id_encuesta)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE rellena (
    email VARCHAR(150),
    id_encuesta INT,
    fecha_respuesta DATE,
    PRIMARY KEY (email, id_encuesta),
    FOREIGN KEY (email) REFERENCES usuario(email)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (id_encuesta) REFERENCES encuesta(id_encuesta)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE responde (
    id_respuesta INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    id_pregunta INT NOT NULL,
    respuesta TEXT,
    FOREIGN KEY (email) REFERENCES usuario(email)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (id_pregunta) REFERENCES pregunta(id_pregunta)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE plato (
    id_plato INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo ENUM('PRIMERO', 'SEGUNDO', 'POSTRE', 'BEBIDA') NOT NULL,
    precio DECIMAL(6,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE menu (
    id_menu INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
	precio DECIMAL(6,2) NOT NULL,
    id_primer_plato INT NOT NULL,
    id_segundo_plato INT NOT NULL,
    id_postre INT NOT NULL,
    id_bebida INT NOT NULL,
    FOREIGN KEY (id_primer_plato) REFERENCES plato(id_plato),
    FOREIGN KEY (id_segundo_plato) REFERENCES plato(id_plato),
    FOREIGN KEY (id_postre) REFERENCES plato(id_plato),
    FOREIGN KEY (id_bebida) REFERENCES plato(id_plato)
);

CREATE TABLE reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    id_menu INT NOT NULL,
    fecha_reserva DATE NOT NULL,
    precio_pagado DECIMAL(6,2),
    estado VARCHAR(50),
    codigo_recogida VARCHAR(50),
    asistido BOOLEAN DEFAULT FALSE,
	metodo_pago VARCHAR(20) DEFAULT NULL,
    estado_pago VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    FOREIGN KEY (email) REFERENCES usuario(email)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE notificacion (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    email_destinatario VARCHAR(150) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    mensaje TEXT,
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    url_accion VARCHAR(255),
    FOREIGN KEY (email_destinatario) REFERENCES usuario(email)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Datos de prueba
INSERT INTO usuario (email, nombre, contrasena, rol, fecha_registro, estado) VALUES
('cocinero_manuel@instituto.com', 'Chef Manuel', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'COCINERO', '2026-01-01', TRUE),
('profe1@instituto.com', 'Maria Lopez', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'PROFESOR', '2026-01-15', TRUE),
('profe2@instituto.com', 'Juan Garcia', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'PROFESOR', '2026-02-01', TRUE),
('profe3@instituto.com', 'Carlos Ruiz', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'PROFESOR', '2026-01-20', TRUE),
('profe4@instituto.com', 'Ana Martinez', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'PROFESOR', '2026-02-10', TRUE),
('profe5@instituto.com', 'Pedro Sanchez', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'PROFESOR', '2026-02-15', TRUE),
('profe6@instituto.com', 'Laura Gomez', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'PROFESOR', '2026-03-01', FALSE),
('cocinero2@instituto.com', 'Chef Sofia', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'COCINERO', '2026-01-05', TRUE);

INSERT INTO usuario (email, nombre, contrasena, rol, fecha_registro, estado) VALUES ('admin@instituto.com', 'ADMIN', '$2a$10$lF6/TEVcza8fTHUq2atoCulPDDSYu68aWKQnrW4cG6EpJVEmwNv.S', 'ADMIN', '2026-01-01', TRUE);

INSERT INTO plato (nombre, descripcion, tipo, precio, activo) VALUES
('Sopa de verduras', 'Sopa casera con verduras de temporada', 'PRIMERO', 3.50, TRUE),
('Ensalada mixta', 'Lechuga, tomate, atun y maiz', 'PRIMERO', 3.00, TRUE),
('Macarrones boloñesa', 'Pasta con salsa boloñesa casera', 'PRIMERO', 4.00, TRUE),
('Pollo asado', 'Pollo asado con patatas', 'SEGUNDO', 5.50, TRUE),
('Merluza a la plancha', 'Merluza fresca con guarnicion', 'SEGUNDO', 6.00, TRUE),
('Tortilla española', 'Tortilla de patatas casera', 'SEGUNDO', 4.50, TRUE),
('Flan casero', 'Flan de huevo casero', 'POSTRE', 2.00, TRUE),
('Fruta del tiempo', 'Fruta variada de temporada', 'POSTRE', 1.50, TRUE),
('Yogur natural', 'Yogur natural con miel', 'POSTRE', 1.50, TRUE),
('Agua', 'Botella de agua 50cl', 'BEBIDA', 1.00, TRUE),
('Zumo de naranja', 'Zumo natural de naranja', 'BEBIDA', 1.50, TRUE),
('Refresco', 'Refresco variado', 'BEBIDA', 1.50, TRUE),
('Crema de calabaza', 'Crema suave de calabaza con croutons', 'PRIMERO', 3.50, TRUE),
('Gazpacho andaluz', 'Gazpacho fresco casero', 'PRIMERO', 3.00, TRUE),
('Lentejas estofadas', 'Lentejas con chorizo y verduras', 'PRIMERO', 4.00, TRUE),
('Arroz con pollo', 'Arroz caldoso con pollo y azafran', 'PRIMERO', 4.50, TRUE),
('Filete de ternera', 'Filete a la plancha con patatas fritas', 'SEGUNDO', 7.00, TRUE),
('Salmon al horno', 'Salmon con verduras al horno', 'SEGUNDO', 7.50, TRUE),
('Albondigas en salsa', 'Albondigas caseras en salsa de tomate', 'SEGUNDO', 5.50, TRUE),
('Pechuga a la plancha', 'Pechuga de pollo con ensalada', 'SEGUNDO', 5.00, TRUE),
('Tarta de queso', 'Tarta de queso al horno estilo vasco', 'POSTRE', 2.50, TRUE),
('Natillas caseras', 'Natillas con galleta', 'POSTRE', 2.00, TRUE),
('Helado', 'Helado de temporada dos bolas', 'POSTRE', 2.50, TRUE),
('Macedonia de frutas', 'Macedonia con zumo natural', 'POSTRE', 2.00, TRUE),
('Cafe', 'Cafe solo o con leche', 'BEBIDA', 1.20, TRUE),
('Infusion', 'Variedad de infusiones', 'BEBIDA', 1.00, TRUE),
('Limonada natural', 'Limonada casera con menta', 'BEBIDA', 1.80, TRUE),
('Croquetas caseras', 'Croquetas de jamon iberico', 'PRIMERO', 4.50, FALSE);


INSERT INTO menu (fecha, precio, id_primer_plato, id_segundo_plato, id_postre, id_bebida) VALUES
('2026-03-03', 15.50, 1, 4, 7, 10),
('2026-04-30', 16.50, 2, 5, 8, 11),
('2026-04-29', 15.50, 3, 6, 9, 12),
('2026-05-05', 15.50, 13, 17, 19, 10),
('2026-05-06', 16.00, 14, 18, 20, 23),
('2026-05-07', 15.50, 15, 19, 21, 24),
('2026-05-08', 16.50, 16, 20, 22, 25),
('2026-05-12', 15.50, 13, 17, 19, 10),
('2026-05-13', 16.00, 14, 18, 20, 23),
('2026-05-14', 15.00, 1,  4,  7,  10),
('2026-05-15', 16.50, 2,  5,  8,  11),
('2026-05-19', 15.50, 3,  6,  9,  12),
('2026-05-20', 16.00, 15, 19, 21, 24),
('2026-05-21', 15.50, 16, 20, 22, 25),
('2026-05-22', 16.00, 13, 18, 19, 23);

INSERT INTO reserva (email, id_menu, fecha_reserva, precio_pagado, estado, codigo_recogida, asistido, metodo_pago, estado_pago) VALUES
('profe1@instituto.com', 1, '2026-03-03', 15.50, 'RECOGIDO',    'AB12CD', TRUE,  'TARJETA', 'PAGADO'),
('profe2@instituto.com', 1, '2026-03-03', 15.50, 'RECOGIDO',    'EF34GH', TRUE,  'CAJA',    'PAGADO'),
('profe3@instituto.com', 1, '2026-03-03', 15.50, 'CANCELADO', 'IJ56KL', FALSE, 'BIZUM',   'PAGADO'),
('profe1@instituto.com', 2, '2026-04-30', 16.50, 'RECOGIDO',    'MN78OP', TRUE,  'TARJETA', 'PAGADO'),
('profe2@instituto.com', 2, '2026-04-30', 16.50, 'RECOGIDO',    'QR90ST', FALSE, 'CAJA',    'PENDIENTE'),
('profe4@instituto.com', 2, '2026-04-30', 16.50, 'RECOGIDO',    'UV12WX', TRUE,  'BIZUM',   'PAGADO'),
('profe1@instituto.com', 3, '2026-04-29', 15.50, 'CANCELADO',    'YZ34AB', TRUE,  'TARJETA', 'PAGADO'),
('profe3@instituto.com', 3, '2026-04-29', 15.50, 'RECOGIDO',    'CD56EF', TRUE,  'CAJA',    'PAGADO'),
('profe5@instituto.com', 3, '2026-04-29', 15.50, 'RECOGIDO',    'GH78IJ', FALSE, 'CAJA',    'PENDIENTE'),
('profe1@instituto.com', 4, '2026-04-16', 15.00, 'RECOGIDO',    'KL90MN', TRUE,  'BIZUM',   'PAGADO'),
('profe2@instituto.com', 5, '2026-05-05', 15.50, 'RECOGIDO',    'OP12QR', FALSE, 'CAJA',    'PENDIENTE'),
('profe3@instituto.com', 5, '2026-05-05', 15.50, 'RECOGIDO',    'ST34UV', FALSE, 'TARJETA', 'PAGADO'),
('profe4@instituto.com', 6, '2026-05-06', 16.00, 'RECOGIDO',    'WX56YZ', FALSE, 'BIZUM',   'PAGADO'),
('profe1@instituto.com', 7, '2026-05-07', 15.50, 'PENDIENTE',    'AB78CD', FALSE, 'CAJA',    'PENDIENTE'),
('profe5@instituto.com', 8, '2026-05-08', 16.50, 'PENDIENTE',    'EF90GH', FALSE, 'TARJETA', 'PAGADO');

INSERT INTO encuesta (titulo, descripcion, fecha_creacion) VALUES
('Satisfaccion con el menu de marzo', 'Queremos saber tu opinion sobre los menus de este mes', '2026-03-01'),
('Que plato nuevo quieres?', 'Estamos pensando en incorporar nuevos platos. Dinos cual prefieres.', '2026-03-02'),
('Valoracion del servicio de abril', 'Cuéntanos tu experiencia con el servicio durante el mes de abril', '2026-04-01'),
('Preferencias para el menu de verano', 'Estamos preparando el menu de verano y queremos tu opinion', '2026-05-01'),
('Horario de la cafeteria', 'Queremos ajustar el horario de apertura segun vuestras necesidades', '2026-05-10');

INSERT INTO pregunta (texto, id_encuesta) VALUES
('¿Como valorarias la calidad de los platos del mes de marzo? (1-5)', 1),
('¿El precio te parece adecuado para la calidad ofrecida?', 1),
('¿Que mejorarías del servicio de la cafeteria?', 1),
('¿Que tipo de plato nuevo te gustaria ver en el menu?', 2),
('¿Preferirías opciones vegetarianas o veganas?', 2),
('¿Te gustaria que hubiera menu especial los viernes?', 2),
('¿Como valorarias la atencion del personal? (1-5)', 3),
('¿Que horario de apertura te vendria mejor?', 3),
('¿Usarias un servicio de reserva online si existiera?', 3),
('¿Recomendarias la cafeteria a otros compañeros?', 1),
('¿Hay algun plato que no volveria a pedir?', 2),
('¿Te gustaria ver mas opciones de postre?', 4),
('¿Como calificarias la limpieza del local? (1-5)', 4),
('¿El tiempo de espera es aceptable?', 4),
('¿Usas la cafeteria todos los dias o solo ocasionalmente?', 5);

INSERT INTO rellena (email, id_encuesta, fecha_respuesta) VALUES
('profe1@instituto.com', 1, '2026-03-10'),
('profe2@instituto.com', 1, '2026-03-11'),
('profe3@instituto.com', 1, '2026-03-12'),
('profe1@instituto.com', 2, '2026-03-15'),
('profe2@instituto.com', 2, '2026-03-16'),
('profe1@instituto.com', 3, '2026-04-10'),
('profe4@instituto.com', 3, '2026-04-11');

INSERT INTO responde (email, id_pregunta, respuesta) VALUES
('profe1@instituto.com', 1, '4 - Muy buenos en general'),
('profe1@instituto.com', 2, 'Si, el precio es justo para lo que ofrecen'),
('profe1@instituto.com', 3, 'Quizas mas variedad en los postres'),
('profe1@instituto.com', 10, 'Si, totalmente'),
('profe2@instituto.com', 1, '3 - Algunos dias mejor que otros'),
('profe2@instituto.com', 2, 'Podria ser algo mas economico'),
('profe2@instituto.com', 3, 'Mas opciones vegetarianas'),
('profe2@instituto.com', 10, 'Si, el ambiente es bueno'),
('profe3@instituto.com', 1, '5 - Excelente calidad'),
('profe3@instituto.com', 2, 'Si, muy razonable'),
('profe3@instituto.com', 3, 'Todo esta bien, solo mas postre'),
('profe3@instituto.com', 10, 'Por supuesto'),
('profe1@instituto.com', 4, 'Me gustaria ver paella los viernes'),
('profe1@instituto.com', 5, 'Si, opciones vegetarianas seria genial'),
('profe1@instituto.com', 6, 'Si, menu especial los viernes suena bien'),
('profe1@instituto.com', 11, 'El gazpacho no me convencio mucho'),
('profe2@instituto.com', 4, 'Mas pescado fresco en el menu'),
('profe2@instituto.com', 5, 'No necesariamente vegano pero si mas verdura'),
('profe2@instituto.com', 6, 'Indiferente'),
('profe2@instituto.com', 11, 'Todos estaban correctos'),
('profe1@instituto.com', 7, '5 - El personal es muy amable'),
('profe1@instituto.com', 8, 'De 8:00 a 16:00 estaria perfecto'),
('profe1@instituto.com', 9, 'Si, lo usaria sin duda'),
('profe4@instituto.com', 7, '4 - Muy buena atencion'),
('profe4@instituto.com', 8, 'Con que abra a las 7:30 me vale'),
('profe4@instituto.com', 9, 'Si, muy comodo');

INSERT INTO notificacion (email_destinatario, tipo, titulo, mensaje, leida, fecha_creacion, url_accion) VALUES
('profe1@instituto.com', 'NUEVA_ENCUESTA',  'Nueva encuesta disponible', 'Se ha publicado la encuesta Satisfaccion con el menu de marzo', FALSE, '2026-03-01 09:00:00', '/profesor/encuestas'),
('profe1@instituto.com', 'RESERVA_LISTA',   'Tu reserva ha sido recogida', 'Tu menu del 2026-03-03 con codigo AB12CD ha sido entregado', TRUE,  '2026-03-03 14:00:00', '/profesor/reservas'),
('profe1@instituto.com', 'AVISO_ADMIN',     'Aviso importante', 'La cafeteria permanecera cerrada el proximo viernes por mantenimiento', FALSE, '2026-04-28 08:00:00', NULL),
('profe2@instituto.com', 'NUEVA_ENCUESTA',  'Nueva encuesta disponible', 'Se ha publicado la encuesta Que plato nuevo quieres', FALSE, '2026-03-02 09:00:00', '/profesor/encuestas'),
('profe3@instituto.com', 'NUEVA_ENCUESTA',  'Nueva encuesta disponible', 'Se ha publicado la encuesta Valoracion del servicio de abril', FALSE, '2026-04-01 09:00:00', '/profesor/encuestas'),
('cocinero_manuel@instituto.com', 'NUEVA_RESERVA', 'Nueva reserva de Maria Lopez', 'La profesora Maria Lopez ha reservado el menu del 2026-03-03. Codigo: AB12CD', TRUE, '2026-03-02 10:00:00', '/cocinero/reservas'),
('cocinero_manuel@instituto.com', 'NUEVA_RESPUESTA_ENCUESTA', 'Nueva respuesta en Satisfaccion con el menu de marzo', 'La profesora Maria Lopez ha respondido la encuesta', TRUE, '2026-03-10 11:00:00', '/cocinero/encuestas'),
('admin@instituto.com', 'NUEVO_USUARIO',    'Nuevo usuario registrado', 'Carlos Ruiz se ha registrado con el rol PROFESOR', TRUE, '2026-01-20 08:30:00', '/admin/usuarios'),
('admin@instituto.com', 'NUEVA_RESERVA',    'Nueva reserva de Juan Garcia', 'El profesor Juan Garcia ha reservado el menu del 2026-04-30. Codigo: QR90ST', FALSE, '2026-04-29 12:00:00', '/cocinero/reservas'),
('profe4@instituto.com', 'NUEVA_ENCUESTA',  'Nueva encuesta disponible', 'Se ha publicado la encuesta Preferencias para el menu de verano', FALSE, '2026-05-01 09:00:00', '/profesor/encuestas'),
('profe5@instituto.com', 'AVISO_ADMIN',     'Aviso importante', 'Recordad que el menu debe reservarse antes de las 10:00', FALSE, '2026-05-10 08:00:00', NULL);
