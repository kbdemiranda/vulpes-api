package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class AssinanteDTO {

    @JsonProperty(value = "id", index = 0)
    private Long id;

    @NotBlank(message = "O nome não pode ser vazio")
    @JsonProperty(value = "nome", index = 1)
    private String nome;

    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Email inválido")
    @JsonProperty(value = "email", index = 2)
    private String email;

    @JsonProperty(value = "plataformas_associadas", index = 3)
    private List<PlataformaResumoDTO> plataformasAssociadas;

    @JsonProperty(value = "valor_por_mes", index = 4)
    private BigDecimal valorPorMes;


    @JsonProperty(value = "cadastrado_em", index = 5)
    private LocalDateTime cadastradoEm;
    @JsonProperty(value = "atualizado_em", index = 6)
    private LocalDateTime atualizadoEm;
    @JsonProperty(value = "excluido_em", index = 7)
    private LocalDateTime excluidoEm;

}
