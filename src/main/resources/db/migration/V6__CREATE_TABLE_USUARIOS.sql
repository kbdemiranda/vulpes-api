create table vulpes.usuarios(
    id bigserial,
    nome text not null,
    sobrenome text not null,
    email text unique not null,
    senha text not null,
    data_ultimo_login timestamp,
    cadastrado_em timestamp not null,
    atualizado_em timestamp,
    excluido_em timestamp,

    constraint pk_usuarios primary key(id)
)