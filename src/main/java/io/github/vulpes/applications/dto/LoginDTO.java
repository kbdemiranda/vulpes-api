package io.github.vulpes.applications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(name = "LoginRequest", description = "Credenciais para autenticação")
public class LoginDTO {

    @Schema(description = "Email do usuário", example = "user@example.com")
    @NotBlank(message = "O email não pode ser vazio")
    private String email;

    @Schema(description = "Senha do usuário", example = "S3nh@F0rte", accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "A senha não pode ser vazia")
    private String senha;

}
