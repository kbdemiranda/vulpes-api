package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Schema(name = "Assinante", description = "Dados de um assinante")
public class AssinanteDTO {

    @Schema(description = "Identificador do assinante", example = "1")
    @JsonProperty(value = "id", index = 0)
    private Long id;

    @NotBlank(message = "O nome não pode ser vazio")
    @Schema(description = "Nome do assinante", example = "João Silva")
    @JsonProperty(value = "nome", index = 1)
    private String nome;

    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Email inválido")
    @Schema(description = "Email do assinante", example = "joao@email.com")
    @JsonProperty(value = "email", index = 2)
    private String email;

    @Schema(description = "Plataformas associadas ao assinante")
    @JsonProperty(value = "plataformas_associadas", index = 3)
    private List<PlataformaResumoDTO> plataformasAssociadas;

    @Schema(description = "Custo mensal estimado", example = "29.90")
    @JsonProperty(value = "valor_por_mes", index = 4)
    private BigDecimal valorPorMes;

    @Schema(description = "Data de criação", example = "2024-08-01T10:00:00")
    @JsonProperty(value = "cadastrado_em", index = 5)
    private LocalDateTime cadastradoEm;
    @Schema(description = "Data de atualização", example = "2024-08-02T10:00:00")
    @JsonProperty(value = "atualizado_em", index = 6)
    private LocalDateTime atualizadoEm;
    @Schema(description = "Data de exclusão (soft delete)", example = "2024-08-03T10:00:00")
    @JsonProperty(value = "excluido_em", index = 7)
    private LocalDateTime excluidoEm;

}
