package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Schema(name = "Assinante", description = "Dados de um assinante")
public class SubscriberDTO {

    @Schema(description = "Identificador do assinante", example = "1")
    @JsonProperty(value = "id", index = 0)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Schema(description = "Subscriber name", example = "João Silva")
    @JsonProperty(value = "nome", index = 1)
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email")
    @Schema(description = "Subscriber email", example = "joao@email.com")
    @JsonProperty(value = "email", index = 2)
    private String email;

    @Schema(description = "Associated platforms")
    @JsonProperty(value = "plataformas_associadas", index = 3)
    private List<PlatformSummaryDTO> associatedPlatforms;

    @Schema(description = "Estimated monthly cost", example = "29.90")
    @JsonProperty(value = "valor_por_mes", index = 4)
    private BigDecimal amountPerMonth;

    @Schema(description = "Creation date", example = "2024-08-01T10:00:00")
    @JsonProperty(value = "cadastrado_em", index = 5)
    private LocalDateTime registeredAt;
    @Schema(description = "Update date", example = "2024-08-02T10:00:00")
    @JsonProperty(value = "atualizado_em", index = 6)
    private LocalDateTime updatedAt;
    @Schema(description = "Deletion date (soft delete)", example = "2024-08-03T10:00:00")
    @JsonProperty(value = "excluido_em", index = 7)
    private LocalDateTime deletedAt;

}
