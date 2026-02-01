-- Adiciona colunas para ciclo de cobrança e dia/mes de cobrança
ALTER TABLE vulpes.plataformas
  ADD COLUMN ciclo_cobranca VARCHAR(20) NOT NULL DEFAULT 'MENSAL',
  ADD COLUMN dia_mes_cobranca VARCHAR(5);
