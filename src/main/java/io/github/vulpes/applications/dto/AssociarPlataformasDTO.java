package io.github.vulpes.applications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "AssociarPlataformasRequest", description = "Requisição para associar plataformas a um assinante")
public class AssociarPlataformasDTO {
    @Schema(description = "IDs das plataformas", example = "[1,2,3]")
    private List<Long> plataformaIds;
}
