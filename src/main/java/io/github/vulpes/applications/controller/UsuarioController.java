package io.github.vulpes.applications.controller;

import io.github.vulpes.applications.dto.UsuarioDTO;
import io.github.vulpes.applications.service.UsuarioService;
import io.github.vulpes.infrastructure.http.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "APIs para gerenciamento de usuários")
@SecurityRequirement(name = "bearer-key")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> cadastrarUsuario(@Valid @RequestBody UsuarioDTO dto){
        UsuarioDTO usuarioDTO = usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.created(URI.create("/usuarios/" + usuarioDTO.getId())).body(usuarioDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> buscarUsuarioPorId(@Parameter(description = "ID do usuário", example = "1") @PathVariable Long id){
        UsuarioDTO usuarioDTO = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(usuarioDTO);
    }

    @GetMapping
    @Operation(summary = "Buscar usuário por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> buscarUsuarioPorEmail(@Parameter(description = "Email do usuário", example = "user@example.com") @RequestParam String email){
        UsuarioDTO usuarioDTO = usuarioService.buscarUsuarioPorEmail(email);
        return ResponseEntity.ok(usuarioDTO);
    }

    @PutMapping("/{id}/senha")
    @Transactional
    @Operation(summary = "Atualizar senha do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha atualizada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> atualizarSenha(@Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
                                            @Valid @RequestBody UsuarioDTO dto){
        usuarioService.atualizarSenha(id, dto.getSenha());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/perfis")
    @Transactional
    @Operation(summary = "Atualizar perfis do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfis atualizados", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> atualizarPerfis(@Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
                                             @Valid @RequestBody UsuarioDTO dto){
        usuarioService.atualizarPerfis(id, dto.getPerfisId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário atualizado",
                    content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> atualizarUsuario(@Parameter(description = "ID do usuário", example = "1") @PathVariable Long id,
                                              @Valid @RequestBody UsuarioDTO dto){
        UsuarioDTO usuarioDTO = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.created(URI.create("/usuarios/" + usuarioDTO.getId())).body(usuarioDTO);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> excluirUsuario(@Parameter(description = "ID do usuário", example = "1") @PathVariable Long id){
        usuarioService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
