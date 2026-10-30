-- ============================================
-- BANCO DE DADOS: LOJA DE PRODUTOS DE PESCA
-- Disciplina: Laboratório de Banco de Dados
-- ============================================

DROP DATABASE IF EXISTS pescaria;
CREATE DATABASE pescaria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE pescaria;

-- --------------------------------------------
-- TABELA: produto
-- --------------------------------------------
CREATE TABLE produto (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100)  NOT NULL,
    marca      VARCHAR(50),
    categoria  VARCHAR(50)   NOT NULL,
    preco      DECIMAL(10,2) NOT NULL,
    estoque    INT           NOT NULL DEFAULT 0,
    foto       VARCHAR(255)
) ENGINE=InnoDB;

-- --------------------------------------------
-- TABELA: cliente
-- --------------------------------------------
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    telefone   VARCHAR(20),
    email      VARCHAR(100)
) ENGINE=InnoDB;

-- --------------------------------------------
-- TABELA: venda (cabeçalho)
-- --------------------------------------------
CREATE TABLE venda (
    id_venda   INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT           NOT NULL,
    data_venda DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total      DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_venda_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
) ENGINE=InnoDB;

-- --------------------------------------------
-- TABELA: item_venda (linhas da venda)
-- --------------------------------------------
CREATE TABLE item_venda (
    id_item        INT AUTO_INCREMENT PRIMARY KEY,
    id_venda       INT           NOT NULL,
    id_produto     INT           NOT NULL,
    quantidade     INT           NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    subtotal       DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_venda_venda
        FOREIGN KEY (id_venda) REFERENCES venda(id_venda) ON DELETE CASCADE,
    CONSTRAINT fk_item_venda_produto
        FOREIGN KEY (id_produto) REFERENCES produto(id_produto)
) ENGINE=InnoDB;



-- Produtos de exemplo
INSERT INTO produto (nome, marca, categoria, preco, estoque, foto) VALUES
('Vara Telescópica 2,10m',       'Marine Sports', 'Vara',    120.00,  15, 'vara1.jpg'),
('Molinete 4000',                'Shimano',       'Molinete',350.00,   8, 'molinete1.jpg'),
('Linha Multifilamento 0,20mm',  'Sufix',         'Linha',    85.00,  30, 'linha1.jpg'),
('Anzol Chinu nº 8 (pacote)',    'Owner',         'Anzol',    12.50, 100, NULL),
('Isca Artificial Popper',       'Rapala',        'Isca',     45.00,  20, NULL);

-- Clientes de exemplo
INSERT INTO cliente (nome, telefone, email) VALUES
('Gustavo Francisco de Araujo', '(11) 98765-4321', 'gustavo@email.com'),
('João Pedro Cavalcante Vasconcelos',   '(67) 6767-6767', 'joao67@email.com');

-- Venda 1: Gustavo comprou 1 vara + 2 anzóis (total = 120 + 25 = 145)
INSERT INTO venda (id_cliente, data_venda, total) VALUES
(1, '2025-01-15 10:30:00', 145.00);

INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES
(1, 1, 1, 120.00, 120.00),
(1, 4, 2,  12.50,  25.00);

-- Venda 2: João comprou 1 molinete + 1 linha (total = 350 + 85 = 435)
INSERT INTO venda (id_cliente, data_venda, total) VALUES
(2, '2025-01-16 14:15:00', 435.00);

INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES
(2, 2, 1, 350.00, 350.00),
(2, 3, 1,  85.00,  85.00);



CREATE VIEW vw_relatorio_vendas AS
SELECT
    c.nome            AS cliente,
    p.nome            AS item,
    p.marca           AS marca,
    p.categoria       AS categoria,
    iv.preco_unitario AS preco,
    iv.quantidade     AS quantidade,
    iv.subtotal       AS total
FROM item_venda iv
JOIN venda   v ON iv.id_venda   = v.id_venda
JOIN cliente c ON v.id_cliente  = c.id_cliente
JOIN produto p ON iv.id_produto = p.id_produto;



SELECT * FROM vw_relatorio_vendas ORDER BY cliente;