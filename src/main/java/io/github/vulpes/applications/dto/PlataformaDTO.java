package io.github.vulpes.applications.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vulpes.domain.enums.TipoServico;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class PlataformaDTO {

    @JsonProperty(value = "id", index = 0)
    private Long id;

    @NotBlank(message = "O nome não pode ser vazio")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres")
    @JsonProperty(value="nome", index = 1)
    private String nome;

    @NotNull(message = "O preço não pode ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
    @JsonProperty(value = "preco", index = 2)
    private BigDecimal preco;

    @URL(message = "URL inválida")
    @JsonProperty(value = "url", index = 3)
    private String url;

    @NotNull(message = "O tipo de serviço não pode ser nulo")
    @JsonProperty(value = "tipo_servico", index = 4)
    private TipoServico tipoServico;

    @NotNull(message = "O total de vagas não pode ser nulo")
    @Min(value = 1, message = "O total de vagas deve ser pelo menos 1")
    @JsonProperty(value = "total_vagas", index = 5)
    private Integer totalVagas;

    @JsonProperty(value = "vagas_disponiveis", index = 6)
    private Integer vagasDisponiveis;

    @JsonProperty(value = "cadastrado_em", index = 7)
    private LocalDateTime cadastradoEm;

    @JsonProperty(value = "atualizado_em", index = 8)
    private LocalDateTime atualizadoEm;

    @JsonProperty(value = "excluido_em", index = 9)
    private LocalDateTime excluidoEm;
}
