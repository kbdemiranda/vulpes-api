package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.PagamentoDTO;
import io.github.vulpes.applications.service.PagamentoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
@Tag(name = "Pagamentos", description = "APIs para gerenciamento de pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> registrarPagamento(@RequestBody PagamentoDTO dto) {
        PagamentoDTO pagamentoDTO = pagamentoService.registrarPagamento(dto);
        return ResponseEntity.created(URI.create("/pagamentos/" + pagamentoDTO.getId())).body(pagamentoDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizarPagamento(@PathVariable Long id, @RequestBody PagamentoDTO dto) {
        PagamentoDTO pagamentoDTO = pagamentoService.atualizarPagamento(id, dto);
        return ResponseEntity.created(URI.create("/pagamentos/" + pagamentoDTO.getId())).body(pagamentoDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultarPagamento(@PathVariable Long id) {
        PagamentoDTO pagamentoDTO = pagamentoService.consultarPagamento(id);
        return ResponseEntity.ok(pagamentoDTO);
    }

    @GetMapping
    public ResponseEntity<?> listarPagamentos(
            @RequestParam(required = false) String nomeAssinante,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        Page<PagamentoDTO> pagamentos = pagamentoService.listarPagamentos(nomeAssinante, PageRequest.of(pagina, quantidade));
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/assinante/{id}")
    public ResponseEntity<?> listarPagamentosAssinante(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        Page<PagamentoDTO> pagamentos = pagamentoService.listarPagamentosAssinante(id, PageRequest.of(pagina, quantidade));
        return ResponseEntity.ok(pagamentos);
    }

}

