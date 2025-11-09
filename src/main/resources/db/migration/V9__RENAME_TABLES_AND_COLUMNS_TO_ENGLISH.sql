-- Migration to rename all tables and columns from Portuguese to English

-- Rename table: plataformas -> platforms
ALTER TABLE vulpes.plataformas RENAME TO platforms;
ALTER TABLE vulpes.platforms RENAME COLUMN nome TO name;
ALTER TABLE vulpes.platforms RENAME COLUMN preco TO price;
ALTER TABLE vulpes.platforms RENAME COLUMN tipo_servico TO service_type;
ALTER TABLE vulpes.platforms RENAME COLUMN total_vagas TO total_slots;
ALTER TABLE vulpes.platforms RENAME COLUMN vagas_disponiveis TO available_slots;
ALTER TABLE vulpes.platforms RENAME COLUMN cadastrado_em TO registered_at;
ALTER TABLE vulpes.platforms RENAME COLUMN atualizado_em TO updated_at;
ALTER TABLE vulpes.platforms RENAME COLUMN excluido_em TO deleted_at;

-- Rename table: assinantes -> subscribers
ALTER TABLE vulpes.assinantes RENAME TO subscribers;
ALTER TABLE vulpes.subscribers RENAME COLUMN nome TO name;
ALTER TABLE vulpes.subscribers RENAME COLUMN cadastrado_em TO registered_at;
ALTER TABLE vulpes.subscribers RENAME COLUMN atualizado_em TO updated_at;
ALTER TABLE vulpes.subscribers RENAME COLUMN excluido_em TO deleted_at;

-- Rename table: assinantes_plataformas -> subscribers_platforms
ALTER TABLE vulpes.assinantes_plataformas RENAME TO subscribers_platforms;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN assinante_id TO subscriber_id;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN plataforma_id TO platform_id;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN cadastrado_em TO registered_at;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN atualizado_em TO updated_at;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN excluido_em TO deleted_at;

-- Rename table: pagamentos -> payments
ALTER TABLE vulpes.pagamentos RENAME TO payments;
ALTER TABLE vulpes.payments RENAME COLUMN assinante_id TO subscriber_id;
ALTER TABLE vulpes.payments RENAME COLUMN valor_pago TO amount_paid;
ALTER TABLE vulpes.payments RENAME COLUMN data_pagamento TO payment_date;
ALTER TABLE vulpes.payments RENAME COLUMN meses_cobertos TO months_covered;
ALTER TABLE vulpes.payments RENAME COLUMN cadastrado_em TO registered_at;
ALTER TABLE vulpes.payments RENAME COLUMN atualizado_em TO updated_at;
ALTER TABLE vulpes.payments RENAME COLUMN excluido_em TO deleted_at;

-- Rename table: status_pagamento_mensal -> monthly_payment_status
ALTER TABLE vulpes.status_pagamento_mensal RENAME TO monthly_payment_status;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN mes TO month;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN ano TO year;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN status_pagamento TO payment_status;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN assinante_id TO subscriber_id;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN pagamento_id TO payment_id;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN cadastrado_em TO registered_at;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN atualizado_em TO updated_at;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN excluido_em TO deleted_at;

-- Rename table: usuarios -> users
ALTER TABLE vulpes.usuarios RENAME TO users;
ALTER TABLE vulpes.users RENAME COLUMN nome TO name;
ALTER TABLE vulpes.users RENAME COLUMN sobrenome TO last_name;
ALTER TABLE vulpes.users RENAME COLUMN senha TO password;
ALTER TABLE vulpes.users RENAME COLUMN data_ultimo_login TO last_login_date;
ALTER TABLE vulpes.users RENAME COLUMN cadastrado_em TO registered_at;
ALTER TABLE vulpes.users RENAME COLUMN atualizado_em TO updated_at;
ALTER TABLE vulpes.users RENAME COLUMN excluido_em TO deleted_at;

-- Rename table: perfis -> profiles
ALTER TABLE vulpes.perfis RENAME TO profiles;
ALTER TABLE vulpes.profiles RENAME COLUMN nome TO name;

-- Rename table: usuarios_perfis -> users_profiles
ALTER TABLE vulpes.usuarios_perfis RENAME TO users_profiles;
ALTER TABLE vulpes.users_profiles RENAME COLUMN usuario_id TO user_id;
ALTER TABLE vulpes.users_profiles RENAME COLUMN perfil_id TO profile_id;
