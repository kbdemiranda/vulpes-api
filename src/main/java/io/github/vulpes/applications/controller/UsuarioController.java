package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.UsuarioDTO;
import io.github.vulpes.applications.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> cadastrarUsuario(@RequestBody UsuarioDTO dto){
        UsuarioDTO usuarioDTO = usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.created(URI.create("/usuarios/" + usuarioDTO.getId())).body(usuarioDTO);
    }
}
