package io.github.vulpes.applications.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {

    @NotBlank(message = "O email não pode ser vazio")
    private String email;
    @NotBlank(message = "A senha não pode ser vazia")
    private String senha;

}
