create table vulpes.usuarios_perfis(
    usuario_id integer not null,
    perfil_id integer not null,

    constraint usuarios_perfis_usuario_fk foreign key (usuario_id) references vulpes.usuarios (id),
    constraint usuarios_perfis_perfil_fk foreign key (perfil_id) references vulpes.perfis (id)
)