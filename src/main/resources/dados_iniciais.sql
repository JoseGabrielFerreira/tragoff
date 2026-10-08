USE tragoff;

-- Produtos. A unidade define em que o consumo é contado.
-- Preço 0.00 = o usuário precisa registrar a compra antes de consumir (caso do vape).
INSERT INTO tipo_cigarro (nome, unidade, preco_unitario) VALUES
('Cigarro', 'cigarros', 0.00),
('Cigarro de palha', 'cigarros', 0.00),
('Tabaco', 'cigarros', 0.00),
('Vape/Cigarro eletrônico', 'tragadas', 0.00);

INSERT INTO gatilho (nome) VALUES
('Café'),
('Ansiedade'),
('Estresse'),
('Álcool'),
('Socialização'),
('Outro');

INSERT INTO pessoa (nome, email, senha) VALUES ('Administrador', 'admin@tragoff.com', 'admin');
INSERT INTO administrador (id, nivel_acesso) VALUES (LAST_INSERT_ID(), 'TOTAL');
