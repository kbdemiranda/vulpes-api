package io.github.vulpes.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
@Schema(name = "ErrorResponse", description = "Estrutura padronizada de erros da API")
public class ErrorResponse {

    @Schema(description = "Data/hora do erro (UTC)", example = "2024-08-01T12:34:56")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP do erro", example = "404")
    @JsonProperty("status")
    private Integer status;

    @Schema(description = "Tipo de erro", example = "VulpesException")
    @JsonProperty("error")
    private String error;

    @Schema(description = "Mensagem explicativa do erro", example = "Plataforma não encontrada")
    @JsonProperty("message")
    private String message;

    @Schema(description = "Caminho da requisição", example = "/plataformas/999")
    @JsonProperty("path")
    private String path;
}

