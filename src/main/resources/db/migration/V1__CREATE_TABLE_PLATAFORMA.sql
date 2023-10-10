create schema if not exists vulpes;

create table vulpes.plataformas
(
    id                bigserial,
    nome              text           not null,
    preco             numeric(10, 2) not null,
    tipo_servico      text           not null,
    url               text,
    total_vagas       int4           not null,
    vagas_disponiveis int4           not null,
    cadastrado_em     timestamptz    not null,
    atualizado_em     timestamptz,
    excluido_em       timestamptz,

    constraint plataforma_pk primary key (id)
);
