package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.AssinanteDTO;
import io.github.vulpes.applications.service.AssinanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/assinantes")
public class AssinanteController {

    private final AssinanteService assinanteService;

    @Autowired
    public AssinanteController(AssinanteService assinanteService) {
        this.assinanteService = assinanteService;
    }

    @GetMapping
    public ResponseEntity<?> listarAssinantes(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        return ResponseEntity.ok(assinanteService.listarAssinantes(nome, PageRequest.of(pagina, quantidade)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAssinante(@PathVariable Long id) {
        return ResponseEntity.ok(assinanteService.buscarAssinante(id));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> cadastrarAssinante(@RequestBody AssinanteDTO dto) {
        AssinanteDTO assinanteDTO = assinanteService.cadastrarAssinante(dto);
        return ResponseEntity.created(URI.create("/assinantes/" + assinanteDTO.getId())).body(assinanteDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizarAssinante(@PathVariable Long id, @RequestBody AssinanteDTO dto) {
        AssinanteDTO assinanteDTO = assinanteService.atualizarAssinante(id, dto);
        return ResponseEntity.created(URI.create("/assinantes/" + assinanteDTO.getId())).body(assinanteDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluirAssinante(@PathVariable Long id) {
        assinanteService.excluirAssinante(id);
        return ResponseEntity.noContent().build();
    }
}

