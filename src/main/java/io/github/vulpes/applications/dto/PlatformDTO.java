package io.github.vulpes.applications.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.github.vulpes.domain.enums.TipoServico;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Schema(name = "Plataforma", description = "Dados de uma plataforma de serviço")
public class PlatformDTO {

    @Schema(description = "Identificador da plataforma", example = "1")
    @JsonProperty(value = "id", index = 0)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    @Schema(description = "Platform name", example = "Netflix")
    @JsonProperty(value="nome", index = 1)
    private String name;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Schema(description = "Monthly price", example = "29.90")
    @JsonProperty(value = "preco", index = 2)
    private BigDecimal price;

    @URL(message = "Invalid URL")
    @Schema(description = "Official URL", example = "https://netflix.com")
    @JsonProperty(value = "url", index = 3)
    private String url;

    @NotNull(message = "Service type cannot be null")
    @Schema(description = "Service type", example = "STREAMING_VIDEO")
    @JsonProperty(value = "tipo_servico", index = 4)
    private TipoServico serviceType;

    @NotNull(message = "Total slots cannot be null")
    @Min(value = 1, message = "Total slots must be at least 1")
    @Schema(description = "Total slots", example = "5")
    @JsonProperty(value = "total_vagas", index = 5)
    private Integer totalSlots;

    @Schema(description = "Available slots", example = "5")
    @JsonProperty(value = "vagas_disponiveis", index = 6)
    private Integer availableSlots;

    @Schema(description = "Creation date", example = "2024-08-01T10:00:00")
    @JsonProperty(value = "cadastrado_em", index = 7)
    private LocalDateTime registeredAt;

    @Schema(description = "Update date", example = "2024-08-02T10:00:00")
    @JsonProperty(value = "atualizado_em", index = 8)
    private LocalDateTime updatedAt;

    @Schema(description = "Deletion date (soft delete)", example = "2024-08-03T10:00:00")
    @JsonProperty(value = "excluido_em", index = 9)
    private LocalDateTime deletedAt;
}
