package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.AssinanteDTO;
import io.github.vulpes.applications.dto.AssociarPlataformasDTO;
import io.github.vulpes.applications.service.AssinanteService;
import io.github.vulpes.infrastructure.http.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;

@RestController
@RequestMapping("/assinantes")
@Tag(name = "Assinantes", description = "APIs para gerenciamento de assinantes")
@SecurityRequirement(name = "bearer-key")
@Validated
public class AssinanteController {

    private final AssinanteService assinanteService;

    @Autowired
    public AssinanteController(AssinanteService assinanteService) {
        this.assinanteService = assinanteService;
    }

    @GetMapping
    @Operation(summary = "Listar assinantes", description = "Retorna lista paginada de assinantes, com filtro opcional por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listarAssinantes(
            @Parameter(description = "Filtro por nome", example = "João") @RequestParam(required = false) String nome,
            @Parameter(description = "Número da página (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Quantidade por página", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int quantidade
//          @Parameter(description = "Ordenação", example = "nome,asc") @RequestParam(defaultValue = "nome,asc") String sort
    ) {
        return ResponseEntity.ok(assinanteService.listarAssinantes(nome, PageRequest.of(pagina, quantidade, Sort.Direction.ASC, "nome")));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar assinante", description = "Retorna um assinante pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assinante encontrado",
                    content = @Content(schema = @Schema(implementation = AssinanteDTO.class))),
            @ApiResponse(responseCode = "404", description = "Assinante não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> buscarAssinante(@Parameter(description = "ID do assinante", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(assinanteService.buscarAssinante(id));
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar assinante", description = "Cria um novo assinante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Assinante criado",
                    content = @Content(schema = @Schema(implementation = AssinanteDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> cadastrarAssinante(@Valid @RequestBody AssinanteDTO dto) {
        AssinanteDTO assinanteDTO = assinanteService.cadastrarAssinante(dto);
        return ResponseEntity.created(URI.create("/assinantes/" + assinanteDTO.getId())).body(assinanteDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar assinante", description = "Atualiza dados de um assinante existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Assinante atualizado",
                    content = @Content(schema = @Schema(implementation = AssinanteDTO.class))),
            @ApiResponse(responseCode = "404", description = "Assinante não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> atualizarAssinante(
            @Parameter(description = "ID do assinante", example = "1") @PathVariable Long id,
            @Valid @RequestBody AssinanteDTO dto) {
        AssinanteDTO assinanteDTO = assinanteService.atualizarAssinante(id, dto);
        return ResponseEntity.created(URI.create("/assinantes/" + assinanteDTO.getId())).body(assinanteDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir assinante", description = "Remove um assinante pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Assinante excluído", content = @Content),
            @ApiResponse(responseCode = "404", description = "Assinante não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> excluirAssinante(@Parameter(description = "ID do assinante", example = "1") @PathVariable Long id) {
        assinanteService.excluirAssinante(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/associar-plataformas")
    @Transactional
    @Operation(summary = "Associar plataformas", description = "Associa múltiplas plataformas a um assinante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Associação realizada"),
            @ApiResponse(responseCode = "400", description = "Erro de validação de negócio",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Assinante/Plataforma não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> associarPlataformas(
            @Parameter(description = "ID do assinante", example = "1") @PathVariable Long id,
            @Valid @RequestBody AssociarPlataformasDTO dto) {
        try {
            assinanteService.associarPlataformas(id, dto.getPlataformaIds());
            return ResponseEntity.ok("Plataformas associadas com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{assinanteId}/desassociar-plataforma/{plataformaId}")
    @Transactional
    @Operation(summary = "Desassociar plataforma", description = "Remove a associação de uma plataforma de um assinante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desassociação realizada"),
            @ApiResponse(responseCode = "400", description = "Erro de validação de negócio",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Assinante/Plataforma não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> desassociarPlataforma(
            @Parameter(description = "ID do assinante", example = "1") @PathVariable Long assinanteId,
            @Parameter(description = "ID da plataforma", example = "2") @PathVariable Long plataformaId) {
        try {
            assinanteService.desassociarPlataforma(assinanteId, plataformaId);
            return ResponseEntity.ok("Plataforma desassociada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
