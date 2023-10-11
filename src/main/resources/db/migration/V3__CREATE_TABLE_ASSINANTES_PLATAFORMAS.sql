create table vulpes.assinantes_plataformas
(
    id            bigserial,
    assinante_id  int4        not null,
    plataforma_id int4        not null,
    cadastrado_em timestamptz not null,
    atualizado_em timestamptz,
    excluido_em   timestamptz,

    constraint pk_assinantes_plataformas primary key (id),

    constraint fk_assinantes_plataformas_assinantes foreign key (assinante_id) references vulpes.assinantes (id),
    constraint fk_assinantes_plataformas_plataformas foreign key (plataforma_id) references vulpes.plataformas (id)
);
