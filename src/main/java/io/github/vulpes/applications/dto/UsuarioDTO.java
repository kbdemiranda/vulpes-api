package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vulpes.domain.models.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@AllArgsConstructor
@NoArgsConstructor
@Data

@JsonInclude(NON_NULL)
@Schema(name = "Usuario", description = "Dados de um usuário do sistema")
public class UsuarioDTO {

    @Schema(description = "Identificador do usuário", example = "42")
    @JsonProperty(value = "id")
    private Long id;
    @Schema(description = "Nome", example = "Kaique")
    @JsonProperty(value = "nome")
    @NotBlank(message = "O nome não pode ser vazio")
    private String nome;
    @Schema(description = "Sobrenome", example = "Miranda")
    @JsonProperty(value = "sobrenome")
    @NotBlank(message = "O sobrenome não pode ser vazio")
    private String sobrenome;
    @Schema(description = "Email", example = "kaique@example.com")
    @JsonProperty(value = "email")
    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Email inválido")
    private String email;
    @Schema(description = "Senha do usuário", example = "S3nh@Segura", accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonProperty(value = "senha")
    @NotBlank(message = "A senha não pode ser vazia")
    private String senha;
    @Schema(description = "IDs dos perfis do usuário", example = "[1,2]")
    @JsonProperty(value = "perfis_id")
    @NotNull(message = "O usuário deve ter pelo menos um perfil")
    private List<Long> perfisId;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.sobrenome = usuario.getSobrenome();
        this.email = usuario.getEmail();
    }
}
