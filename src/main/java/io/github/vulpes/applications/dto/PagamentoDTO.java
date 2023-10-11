package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class PagamentoDTO {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "assinante_id")
    @NotNull(message = "O id do assinante é obrigatório")
    private Long assinanteId;

    @JsonProperty(value = "valor_pago")
    @NotNull(message = "O valor pago é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valorPago;

    @JsonProperty(value = "meses_cobertos")
    @NotNull(message = "O número de meses cobertos é obrigatório")
    @Min(value = 1, message = "Deve cobrir pelo menos um mês")
    private Integer mesesCobertos;

    @JsonProperty(value = "meses")
    private List<String> meses;

    @JsonProperty(value = "data_pagamento")
    @NotNull(message = "A data de pagamento é obrigatória")
    @PastOrPresent(message = "A data de pagamento deve ser no presente ou no passado")
    private LocalDateTime dataPagamento;
    @JsonProperty(value = "cadastrado_em")
    private LocalDateTime cadastradoEm;
    @JsonProperty(value = "atualizado_em")
    private LocalDateTime atualizadoEm;
    @JsonProperty(value = "excluido_em")
    private LocalDateTime excluidoEm;
}
