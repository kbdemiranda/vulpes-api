package io.github.vulpes.applications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "PlataformaResumo", description = "Platform summary associated with subscriber")
public class PlatformSummaryDTO {

    @Schema(description = "Platform ID", example = "1")
    private Long id;

    @Schema(description = "Platform name", example = "Netflix")
    private String name;

    @Schema(description = "Total monthly price", example = "29.90")
    private BigDecimal monthlyPrice;

    @Schema(description = "Individual price per subscriber", example = "9.97")
    private BigDecimal individualPrice;
}
