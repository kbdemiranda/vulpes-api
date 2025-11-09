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
public class PaymentDTO {

    @Schema(description = "Identificador do pagamento", example = "10")
    @JsonProperty(value = "id")
    private Long id;

    @Schema(description = "Subscriber ID", example = "1")
    @JsonProperty(value = "assinante_id")
    @NotNull(message = "Subscriber ID is required")
    private Long subscriberId;

    @Schema(description = "Amount paid", example = "59.80")
    @JsonProperty(value = "valor_pago")
    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amountPaid;

    @Schema(description = "Number of months covered", example = "2")
    @JsonProperty(value = "meses_cobertos")
    @NotNull(message = "Number of months covered is required")
    @Min(value = 1, message = "Must cover at least one month")
    private Integer monthsCovered;

    @Schema(description = "Reference months", example = "[\"2024-07\", \"2024-08\"]")
    @JsonProperty(value = "meses")
    private List<String> months;

    @Schema(description = "Payment date", example = "2024-08-01T12:00:00")
    @JsonProperty(value = "data_pagamento")
    @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date must be in the present or past")
    private LocalDateTime paymentDate;
    @Schema(description = "Creation date", example = "2024-08-01T12:00:01")
    @JsonProperty(value = "cadastrado_em")
    private LocalDateTime registeredAt;
    @Schema(description = "Update date", example = "2024-08-02T09:00:00")
    @JsonProperty(value = "atualizado_em")
    private LocalDateTime updatedAt;
    @Schema(description = "Deletion date (soft delete)", example = "2024-08-03T10:00:00")
    @JsonProperty(value = "excluido_em")
    private LocalDateTime deletedAt;
}
