package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.PagamentoDTO;
import io.github.vulpes.applications.service.PagamentoService;
import io.github.vulpes.infrastructure.http.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
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
@RequestMapping("/pagamentos")
@Tag(name = "Pagamentos", description = "APIs para gerenciamento de pagamentos")
@SecurityRequirement(name = "bearer-key")
@Validated
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Registrar pagamento", description = "Cria um novo pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento criado",
                    content = @Content(schema = @Schema(implementation = PagamentoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> registrarPagamento(@Valid @RequestBody PagamentoDTO dto) {
        PagamentoDTO pagamentoDTO = pagamentoService.registrarPagamento(dto);
        return ResponseEntity.created(URI.create("/pagamentos/" + pagamentoDTO.getId())).body(pagamentoDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar pagamento", description = "Atualiza um pagamento existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento atualizado",
                    content = @Content(schema = @Schema(implementation = PagamentoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> atualizarPagamento(
            @Parameter(description = "ID do pagamento", example = "10") @PathVariable Long id,
            @Valid @RequestBody PagamentoDTO dto) {
        PagamentoDTO pagamentoDTO = pagamentoService.atualizarPagamento(id, dto);
        return ResponseEntity.created(URI.create("/pagamentos/" + pagamentoDTO.getId())).body(pagamentoDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar pagamento", description = "Recupera um pagamento pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado",
                    content = @Content(schema = @Schema(implementation = PagamentoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> consultarPagamento(@Parameter(description = "ID do pagamento", example = "10") @PathVariable Long id) {
        PagamentoDTO pagamentoDTO = pagamentoService.consultarPagamento(id);
        return ResponseEntity.ok(pagamentoDTO);
    }

    @GetMapping
    @Operation(summary = "Listar pagamentos", description = "Retorna lista paginada de pagamentos, com filtro opcional por nome do assinante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listarPagamentos(
            @Parameter(description = "Filtro por nome do assinante", example = "João") @RequestParam(required = false) String nomeAssinante,
            @Parameter(description = "Número da página (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Quantidade por página", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int quantidade
    ) {
        Page<PagamentoDTO> pagamentos = pagamentoService.listarPagamentos(nomeAssinante, PageRequest.of(pagina, quantidade));
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/assinante/{id}")
    @Operation(summary = "Listar pagamentos por assinante", description = "Retorna lista paginada de pagamentos de um assinante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Assinante não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> listarPagamentosAssinante(
            @Parameter(description = "ID do assinante", example = "1") @PathVariable Long id,
            @Parameter(description = "Número da página (0..N)", example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Quantidade por página", example = "10")
            @Min(1) @RequestParam(defaultValue = "10") int quantidade
    ) {
        Page<PagamentoDTO> pagamentos = pagamentoService.listarPagamentosAssinante(id, PageRequest.of(pagina, quantidade));
        return ResponseEntity.ok(pagamentos);
    }

}
