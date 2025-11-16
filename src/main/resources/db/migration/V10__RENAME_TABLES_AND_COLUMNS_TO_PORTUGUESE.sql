-- USERS
ALTER TABLE vulpes.users RENAME COLUMN name TO nome;
ALTER TABLE vulpes.users RENAME COLUMN last_name TO sobrenome;
ALTER TABLE vulpes.users RENAME COLUMN password TO senha;
ALTER TABLE vulpes.users RENAME COLUMN last_login_date TO data_ultimo_login;
ALTER TABLE vulpes.users RENAME COLUMN registered_at TO cadastrado_em;
ALTER TABLE vulpes.users RENAME COLUMN updated_at TO atualizado_em;
ALTER TABLE vulpes.users RENAME COLUMN deleted_at TO excluido_em;
ALTER TABLE vulpes.users RENAME TO usuarios;

-- PROFILES
ALTER TABLE vulpes.profiles RENAME COLUMN name TO nome;
ALTER TABLE vulpes.profiles RENAME TO perfis;

-- USERS_PROFILES
ALTER TABLE vulpes.users_profiles RENAME COLUMN user_id TO usuario_id;
ALTER TABLE vulpes.users_profiles RENAME COLUMN profile_id TO perfil_id;
ALTER TABLE vulpes.users_profiles RENAME TO usuarios_perfis;

-- PLATFORMS
ALTER TABLE vulpes.platforms RENAME COLUMN name TO nome;
ALTER TABLE vulpes.platforms RENAME COLUMN price TO preco;
ALTER TABLE vulpes.platforms RENAME COLUMN service_type TO tipo_servico;
ALTER TABLE vulpes.platforms RENAME COLUMN total_slots TO total_vagas;
ALTER TABLE vulpes.platforms RENAME COLUMN available_slots TO vagas_disponiveis;
ALTER TABLE vulpes.platforms RENAME COLUMN registered_at TO cadastrado_em;
ALTER TABLE vulpes.platforms RENAME COLUMN updated_at TO atualizado_em;
ALTER TABLE vulpes.platforms RENAME COLUMN deleted_at TO excluido_em;
ALTER TABLE vulpes.platforms RENAME TO plataformas;

-- SUBSCRIBERS
ALTER TABLE vulpes.subscribers RENAME COLUMN name TO nome;
ALTER TABLE vulpes.subscribers RENAME COLUMN registered_at TO cadastrado_em;
ALTER TABLE vulpes.subscribers RENAME COLUMN updated_at TO atualizado_em;
ALTER TABLE vulpes.subscribers RENAME COLUMN deleted_at TO excluido_em;
ALTER TABLE vulpes.subscribers RENAME TO assinantes;

-- SUBSCRIBERS_PLATFORMS
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN subscriber_id TO assinante_id;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN platform_id TO plataforma_id;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN registered_at TO cadastrado_em;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN updated_at TO atualizado_em;
ALTER TABLE vulpes.subscribers_platforms RENAME COLUMN deleted_at TO excluido_em;
ALTER TABLE vulpes.subscribers_platforms RENAME TO assinantes_plataformas;

-- PAYMENTS
ALTER TABLE vulpes.payments RENAME COLUMN subscriber_id TO assinante_id;
ALTER TABLE vulpes.payments RENAME COLUMN amount_paid TO valor_pago;
ALTER TABLE vulpes.payments RENAME COLUMN payment_date TO data_pagamento;
ALTER TABLE vulpes.payments RENAME COLUMN months_covered TO meses_cobertos;
ALTER TABLE vulpes.payments RENAME COLUMN registered_at TO cadastrado_em;
ALTER TABLE vulpes.payments RENAME COLUMN updated_at TO atualizado_em;
ALTER TABLE vulpes.payments RENAME COLUMN deleted_at TO excluido_em;
ALTER TABLE vulpes.payments RENAME TO pagamentos;

-- MONTHLY_PAYMENT_STATUS
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN month TO mes;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN year TO ano;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN payment_status TO status_pagamento;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN subscriber_id TO assinante_id;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN payment_id TO pagamento_id;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN registered_at TO cadastrado_em;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN updated_at TO atualizado_em;
ALTER TABLE vulpes.monthly_payment_status RENAME COLUMN deleted_at TO excluido_em;
ALTER TABLE vulpes.monthly_payment_status RENAME TO status_pagamento_mensal;
