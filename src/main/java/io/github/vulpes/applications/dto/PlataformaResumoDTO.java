package io.github.vulpes.applications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "PlataformaResumo", description = "Resumo de plataforma associada ao assinante")
public class PlataformaResumoDTO {

    @Schema(description = "ID da plataforma", example = "1")
    private Long id;

    @Schema(description = "Nome da plataforma", example = "Netflix")
    private String nome;

    @Schema(description = "Preço mensal total", example = "29.90")
    private BigDecimal precoMensal;

    @Schema(description = "Preço individual por assinante", example = "9.97")
    private BigDecimal precoIndividual;
}
