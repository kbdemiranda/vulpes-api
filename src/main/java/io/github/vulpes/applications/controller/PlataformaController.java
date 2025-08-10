package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.PlataformaDTO;
import io.github.vulpes.applications.service.PlataformaService;
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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.net.URI;

@RestController
@RequestMapping("/plataformas")
@Tag(name = "Plataformas", description = "APIs para gerenciamento de plataformas")
@SecurityRequirement(name = "bearer-key")
@Validated
public class PlataformaController {

    private final PlataformaService plataformaService;

    public PlataformaController(PlataformaService plataformaService) {
        this.plataformaService = plataformaService;
    }

    @GetMapping
    @Operation(summary = "Listar plataformas", description = "Retorna lista paginada de plataformas, com filtro opcional por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listarPlataformas(
            @Parameter(description = "Filtro por nome", example = "Netflix") @RequestParam(required = false) String nome,
            @Parameter(description = "Número da página (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Quantidade por página", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int quantidade
    ) {
        return ResponseEntity.ok(plataformaService.listarPlataformas(nome, PageRequest.of(pagina, quantidade)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar plataforma", description = "Retorna uma plataforma pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plataforma encontrada",
                    content = @Content(schema = @Schema(implementation = PlataformaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plataforma não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> buscarPlataforma(@Parameter(description = "ID da plataforma", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(plataformaService.buscarPlataforma(id));
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar plataforma", description = "Cria uma nova plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plataforma criada",
                    content = @Content(schema = @Schema(implementation = PlataformaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> cadastrarPlataforma(@Valid @RequestBody PlataformaDTO dto) {

        PlataformaDTO plataformaDTO = plataformaService.cadastrarPlataforma(dto);
        return ResponseEntity.created(URI.create("/plataformas/" + plataformaDTO.getId())).body(plataformaDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar plataforma", description = "Atualiza dados de uma plataforma existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plataforma atualizada",
                    content = @Content(schema = @Schema(implementation = PlataformaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plataforma não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> atualizarPlataforma(
            @Parameter(description = "ID da plataforma", example = "1") @PathVariable Long id,
            @Valid @RequestBody PlataformaDTO dto) {
        PlataformaDTO plataformaDTO = plataformaService.atualizarPlataforma(id, dto);
        return ResponseEntity.created(URI.create("/plataformas/" + plataformaDTO.getId())).body(plataformaDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir plataforma", description = "Remove uma plataforma pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plataforma excluída", content = @Content),
            @ApiResponse(responseCode = "404", description = "Plataforma não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> excluirPlataforma(@Parameter(description = "ID da plataforma", example = "1") @PathVariable Long id) {
        plataformaService.excluirPlataforma(id);
        return ResponseEntity.noContent().build();
    }
}
