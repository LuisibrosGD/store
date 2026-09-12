INSERT IGNORE INTO usuarios (nombre, email, password, role) VALUES
    ('Carlos Pérez', 'carlos@email.com', '$2a$10$84Tmt.QQT5nHBwRLy6aQYurYqjbpebC/GpEw/guR36JTscCyJ087q', 'ADMIN'),
    ('María López', 'maria@email.com', '$2a$10$XSbAOBHrSH5AT/f2Szm18.M9ZaGawXrx9s4MPSLPCAUOHpbjTJpgG', 'CLIENTE'),
    ('Juan Gómez', 'juan@email.com', '$2a$10$2Fp3rutuVnR5MC.mVUtG6OTmA9TtOsmLWM7nIoHPGA01P.orgFpdS', 'CLIENTE');

INSERT IGNORE INTO categorias (id, nombre, padre_id) VALUES
    (1, 'Electrónica', NULL),
    (2, 'Celulares', 1),
    (3, 'Laptops', 1),
    (4, 'Ropa', NULL),
    (5, 'Camisas', 4),
    (6, 'Hogar', NULL);

INSERT IGNORE INTO productos (nombre, precio, stock, categoria_id) VALUES
    ('iPhone 15', 999.99, 50, 2),
    ('Samsung Galaxy S24', 899.99, 30, 2),
    ('MacBook Pro', 1999.99, 15, 3),
    ('Dell XPS 13', 1299.99, 20, 3),
    ('Camisa Azul', 29.99, 100, 5),
    ('Camisa Roja', 34.99, 80, 5);
