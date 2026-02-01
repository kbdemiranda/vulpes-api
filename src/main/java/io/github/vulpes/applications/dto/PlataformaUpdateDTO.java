package io.github.vulpes.applications.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vulpes.domain.enums.TipoServico;
import io.github.vulpes.domain.enums.CicloCobranca;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Schema(name = "Plataforma", description = "Dados de uma plataforma de serviço")
public class PlataformaUpdateDTO {

    @Schema(description = "Identificador da plataforma", example = "1")
    @JsonProperty(value = "id", index = 0)
    private Long id;

    @NotBlank(message = "O nome não pode ser vazio")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres")
    @Schema(description = "Nome da plataforma", example = "Netflix")
    @JsonProperty(value="nome", index = 1)
    private String nome;

    @NotNull(message = "O preço não pode ser nulo")
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

    @Schema(description = "Ciclo de cobrança da plataforma", example = "MENSAL")
    @JsonProperty(value = "ciclo_cobranca", index = 6)
    private CicloCobranca cicloCobranca;

    @Schema(description = "Dia e mês da cobrança para ciclos ANUAL/SEMETRAL (MM-dd)", example = "01-25")
    @JsonProperty(value = "dia_mes_cobranca", index = 7)
    private String diaMesCobranca;
}
