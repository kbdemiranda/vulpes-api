package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.PlataformaDTO;
import io.github.vulpes.applications.service.PlataformaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/plataformas")
public class PlataformaController {

    private final PlataformaService plataformaService;

    public PlataformaController(PlataformaService plataformaService) {
        this.plataformaService = plataformaService;
    }

    @GetMapping
    public ResponseEntity<?> listarPlataformas(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        return ResponseEntity.ok(plataformaService.listarPlataformas(nome, PageRequest.of(pagina, quantidade)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPlataforma(@PathVariable Long id) {
        return ResponseEntity.ok(plataformaService.buscarPlataforma(id));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> cadastrarPlataforma(@RequestBody PlataformaDTO dto) {

        PlataformaDTO plataformaDTO = plataformaService.cadastrarPlataforma(dto);
        return ResponseEntity.created(URI.create("/plataformas/" + plataformaDTO.getId())).body(plataformaDTO);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizarPlataforma(@PathVariable Long id, @RequestBody PlataformaDTO dto) {
        PlataformaDTO plataformaDTO = plataformaService.atualizarPlataforma(id, dto);
        return ResponseEntity.created(URI.create("/plataformas/" + plataformaDTO.getId())).body(plataformaDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluirPlataforma(@PathVariable Long id) {
        plataformaService.excluirPlataforma(id);
        return ResponseEntity.noContent().build();
    }
}
