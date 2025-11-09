package io.github.vulpes.applications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "AssociarPlataformasRequest", description = "Request to associate platforms to a subscriber")
public class AssociatePlatformsDTO {
    @Schema(description = "Platform IDs", example = "[1,2,3]")
    private List<Long> platformIds;
}
