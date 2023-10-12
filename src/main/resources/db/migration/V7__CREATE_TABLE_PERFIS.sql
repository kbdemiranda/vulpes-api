create table vulpes.perfis(
    id bigserial,
    nome text not null,

    constraint pk_perfis primary key(id)
);

INSERT INTO vulpes.perfis (nome) VALUES ('ROLE_ADMIN');
INSERT INTO vulpes.perfis (nome) VALUES ('ROLE_USER');
INSERT INTO vulpes.perfis (nome) VALUES ('ROLE_MANAGER');
INSERT INTO vulpes.perfis (nome) VALUES ('ROLE_GUEST');
