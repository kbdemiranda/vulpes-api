package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Schema(name = "Pagamento", description = "Dados de um pagamento")
public class PagamentoDTO {

    @Schema(description = "Identificador do pagamento", example = "10")
    @JsonProperty(value = "id")
    private Long id;

    @Schema(description = "ID do assinante", example = "1")
    @JsonProperty(value = "assinante_id")
    @NotNull(message = "O id do assinante é obrigatório")
    private Long assinanteId;

    @Schema(description = "Valor pago", example = "59.80")
    @JsonProperty(value = "valor_pago")
    @NotNull(message = "O valor pago é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valorPago;

    @Schema(description = "Quantidade de meses cobertos", example = "2")
    @JsonProperty(value = "meses_cobertos")
    @NotNull(message = "O número de meses cobertos é obrigatório")
    @Min(value = 1, message = "Deve cobrir pelo menos um mês")
    private Integer mesesCobertos;

    @Schema(description = "Meses de referência", example = "[\"2024-07\", \"2024-08\"]")
    @JsonProperty(value = "meses")
    private List<String> meses;

    @Schema(description = "Data do pagamento", example = "2024-08-01T12:00:00")
    @JsonProperty(value = "data_pagamento")
    @NotNull(message = "A data de pagamento é obrigatória")
    @PastOrPresent(message = "A data de pagamento deve ser no presente ou no passado")
    private LocalDateTime dataPagamento;
    @Schema(description = "Data de criação", example = "2024-08-01T12:00:01")
    @JsonProperty(value = "cadastrado_em")
    private LocalDateTime cadastradoEm;
    @Schema(description = "Data de atualização", example = "2024-08-02T09:00:00")
    @JsonProperty(value = "atualizado_em")
    private LocalDateTime atualizadoEm;
    @Schema(description = "Data de exclusão (soft delete)", example = "2024-08-03T10:00:00")
    @JsonProperty(value = "excluido_em")
    private LocalDateTime excluidoEm;
}
