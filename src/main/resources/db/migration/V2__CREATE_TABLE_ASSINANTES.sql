CREATE TABLE vulpes.assinantes
(
    id            bigserial,
    nome          text        not null,
    email         text unique not null,
    cadastrado_em timestamptz not null,
    atualizado_em timestamptz,
    excluido_em   timestamptz,

    constraint assinantes_pk primary key (id)
);
