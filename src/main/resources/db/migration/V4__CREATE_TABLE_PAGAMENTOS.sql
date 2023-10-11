create table vulpes.pagamentos
(
    id            bigserial,
    assinante_id  int4 not null,
    valor_pago    numeric(10, 2) not null,
    data_pagamento timestamptz not null,
    meses_cobertos int4 not null,
    cadastrado_em timestamptz not null,
    atualizado_em timestamptz,
    excluido_em   timestamptz,

    constraint pk_pagamentos primary key (id),
    constraint fk_pagamentos_assinantes foreign key (assinante_id) references vulpes.assinantes (id)
);
