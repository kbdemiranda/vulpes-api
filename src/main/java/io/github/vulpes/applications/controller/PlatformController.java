package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.PlatformDTO;
import io.github.vulpes.applications.service.PlatformService;
import io.github.vulpes.infrastructure.http.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;

@RestController
@RequestMapping("/platforms")
@Tag(name = "Platforms", description = "APIs for platform management")
@SecurityRequirement(name = "bearer-key")
@Validated
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    @Operation(summary = "List platforms", description = "Returns paginated list of platforms, with optional name filter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listPlatforms(
            @Parameter(description = "Filter by name", example = "Netflix") @RequestParam(required = false) String name,
            @Parameter(description = "Page number (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Items per page", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(platformService.listPlatforms(name, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get platform", description = "Returns a platform by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Platform found",
                    content = @Content(schema = @Schema(implementation = PlatformDTO.class))),
            @ApiResponse(responseCode = "404", description = "Platform not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getPlatform(@Parameter(description = "Platform ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(platformService.getPlatform(id));
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Create platform", description = "Creates a new platform")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Platform created",
                    content = @Content(schema = @Schema(implementation = PlatformDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> createPlatform(@Valid @RequestBody PlatformDTO dto) {

        PlatformDTO platformDTO = platformService.createPlatform(dto);
        return ResponseEntity.created(URI.create("/platforms/" + platformDTO.getId())).body(platformDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Update platform", description = "Updates data of an existing platform")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Platform updated",
                    content = @Content(schema = @Schema(implementation = PlatformDTO.class))),
            @ApiResponse(responseCode = "404", description = "Platform not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> updatePlatform(
            @Parameter(description = "Platform ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody PlatformDTO dto) {
        PlatformDTO platformDTO = platformService.updatePlatform(id, dto);
        return ResponseEntity.created(URI.create("/platforms/" + platformDTO.getId())).body(platformDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Delete platform", description = "Removes a platform by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Platform deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Platform not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> deletePlatform(@Parameter(description = "Platform ID", example = "1") @PathVariable Long id) {
        platformService.deletePlatform(id);
        return ResponseEntity.noContent().build();
    }
}
