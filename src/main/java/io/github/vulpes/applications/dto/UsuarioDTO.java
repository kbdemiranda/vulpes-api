package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vulpes.domain.models.Usuario;
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
public class UsuarioDTO {

    @JsonProperty(value = "id")
    private Long id;
    @JsonProperty(value = "nome")
    @NotBlank(message = "O nome não pode ser vazio")
    private String nome;
    @JsonProperty(value = "sobrenome")
    @NotBlank(message = "O sobrenome não pode ser vazio")
    private String sobrenome;
    @JsonProperty(value = "email")
    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Email inválido")
    private String email;
    @JsonProperty(value = "senha")
    @NotBlank(message = "A senha não pode ser vazia")
    private String senha;
    @JsonProperty(value = "perfis_id")
    @NotNull(message = "O usuário deve ter pelo menos um perfil")
    private List<Long> perfisId;

    public UsuarioDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.sobrenome = usuario.getSobrenome();
        this.email = usuario.getEmail();
    }
}
