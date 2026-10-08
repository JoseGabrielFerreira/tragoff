-- Rode UMA vez, no banco que já existe, DEPOIS de iniciar o projeto uma vez
-- (o Spring cria as colunas novas sozinho) e ANTES de usar o sistema.
USE tragoff;
SET SQL_SAFE_UPDATES = 0;

-- 1) Gasto dos registros antigos = quantidade x preço do tipo (precisa vir ANTES de mudar os preços)
UPDATE registro_cigarro r
JOIN tipo_cigarro t ON r.tipo_cigarro_id = t.id
SET r.valor_gasto = r.quantidade * t.preco_unitario
WHERE r.valor_gasto = 0;

-- 2) Nomes e unidades dos produtos (Charuto vira Tabaco)
UPDATE tipo_cigarro SET nome = 'Cigarro', unidade = 'cigarros' WHERE nome = 'Cigarro tradicional';
UPDATE tipo_cigarro SET unidade = 'cigarros' WHERE nome = 'Cigarro de palha';
UPDATE tipo_cigarro SET nome = 'Tabaco', unidade = 'cigarros' WHERE nome = 'Charuto';
UPDATE tipo_cigarro SET nome = 'Vape/Cigarro eletrônico', unidade = 'tragadas', preco_unitario = 0
WHERE nome = 'Cigarro eletrônico/vape';

-- 3) Metas antigas eram todas em cigarros
UPDATE meta SET unidade = 'cigarros' WHERE unidade IS NULL;

SET SQL_SAFE_UPDATES = 1;
