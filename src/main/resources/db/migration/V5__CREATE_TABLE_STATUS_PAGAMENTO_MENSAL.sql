create table vulpes.status_pagamento_mensal
(
    id              bigserial,
    assinante_id    int4 not null,
    pagamento_id    int4 not null,
    mes             int4 not null,
    ano             int4 not null,
    status_pagamento text not null,
    cadastrado_em   timestamptz not null,
    atualizado_em   timestamptz,
    excluido_em     timestamptz,

    constraint pk_status_pagamento_mensal primary key (id),
    constraint fk_status_pagamento_mensal_assinantes foreign key (assinante_id) references vulpes.assinantes (id),
    constraint fk_status_pagamento_mensal_pagamentos foreign key (pagamento_id) references vulpes.pagamentos (id)
);
