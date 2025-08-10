package io.github.vulpes.applications.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.github.vulpes.domain.enums.TipoServico;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Schema(name = "Plataforma", description = "Dados de uma plataforma de serviço")
public class PlataformaDTO {

    @Schema(description = "Identificador da plataforma", example = "1")
    @JsonProperty(value = "id", index = 0)
    private Long id;

    @NotBlank(message = "O nome não pode ser vazio")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres")
    @Schema(description = "Nome da plataforma", example = "Netflix")
    @JsonProperty(value="nome", index = 1)
    private String nome;

    @NotNull(message = "O preço não pode ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    @Schema(description = "Preço mensal", example = "29.90")
    @JsonProperty(value = "preco", index = 2)
    private BigDecimal preco;

    @URL(message = "URL inválida")
    @Schema(description = "URL oficial", example = "https://netflix.com")
    @JsonProperty(value = "url", index = 3)
    private String url;

    @NotNull(message = "O tipo de serviço não pode ser nulo")
    @Schema(description = "Tipo de serviço", example = "STREAMING_VIDEO")
    @JsonProperty(value = "tipo_servico", index = 4)
    private TipoServico tipoServico;

    @NotNull(message = "O total de vagas não pode ser nulo")
    @Min(value = 1, message = "O total de vagas deve ser pelo menos 1")
    @Schema(description = "Total de vagas", example = "5")
    @JsonProperty(value = "total_vagas", index = 5)
    private Integer totalVagas;

    @Schema(description = "Vagas disponíveis", example = "5")
    @JsonProperty(value = "vagas_disponiveis", index = 6)
    private Integer vagasDisponiveis;

    @Schema(description = "Data de criação", example = "2024-08-01T10:00:00")
    @JsonProperty(value = "cadastrado_em", index = 7)
    private LocalDateTime cadastradoEm;

    @Schema(description = "Data de atualização", example = "2024-08-02T10:00:00")
    @JsonProperty(value = "atualizado_em", index = 8)
    private LocalDateTime atualizadoEm;

    @Schema(description = "Data de exclusão (soft delete)", example = "2024-08-03T10:00:00")
    @JsonProperty(value = "excluido_em", index = 9)
    private LocalDateTime excluidoEm;
}
