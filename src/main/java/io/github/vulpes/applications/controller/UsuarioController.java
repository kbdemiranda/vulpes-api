package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.UsuarioDTO;
import io.github.vulpes.applications.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "APIs para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO dto){
        UsuarioDTO usuarioDTO = usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.created(URI.create("/usuarios/" + usuarioDTO.getId())).body(usuarioDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Long id){
        UsuarioDTO usuarioDTO = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping
    public ResponseEntity<?> buscarUsuarioPorEmail(@RequestParam String email){
        UsuarioDTO usuarioDTO = usuarioService.buscarUsuarioPorEmail(email);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PutMapping("/{id}/senha")
    @Transactional
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @RequestBody UsuarioDTO dto){
        usuarioService.atualizarSenha(id, dto.getSenha());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/perfis")
    @Transactional
    public ResponseEntity<?> atualizarPerfis(@PathVariable Long id, @RequestBody UsuarioDTO dto){
        usuarioService.atualizarPerfis(id, dto.getPerfisId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto){
        UsuarioDTO usuarioDTO = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.created(URI.create("/usuarios/" + usuarioDTO.getId())).body(usuarioDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluirUsuario(@PathVariable Long id){
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
