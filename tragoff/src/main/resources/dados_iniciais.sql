USE tragoff;

INSERT INTO tipo_cigarro (nome, preco_unitario) VALUES
('Cigarro tradicional', 0.70),
('Cigarro eletrônico/vape', 1.50),
('Charuto', 5.00),
('Cigarro de palha', 0.50);

INSERT INTO gatilho (nome) VALUES
('Café'),
('Ansiedade'),
('Estresse'),
('Álcool'),
('Socialização'),
('Outro');


USE tragoff;
INSERT INTO pessoa (nome, email, senha) VALUES ('Administrador', 'admin@tragoff.com', 'admin');
INSERT INTO administrador (id, nivel_acesso) VALUES (LAST_INSERT_ID(), 'TOTAL');