package io.github.vulpes.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CicloCobranca", description = "Ciclo de cobrança da plataforma (em português)")
public enum CicloCobranca {
    MENSAL,
    SEMESTRAL,
    ANUAL
}
