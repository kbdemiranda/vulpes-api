package io.github.vulpes.applications.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlataformaResumoDTO {

    private Long id;
    private String nome;
    private BigDecimal precoMensal;
    private BigDecimal precoIndividual;
}
