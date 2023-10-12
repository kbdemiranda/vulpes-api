package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.UsuarioDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsuarioService extends UserDetailsService {

    UsuarioDTO cadastrarUsuario(UsuarioDTO dto);
    UsuarioDTO buscarUsuarioPorId(Long id);
    UsuarioDTO buscarUsuarioPorEmail(String email);
    UsuarioDTO atualizarUsuario(Long id, UsuarioDTO dto);

    void atualizarSenha(Long id, String senha);
    void atualizarPerfis(Long id, List<Long> perfis);
    void excluirUsuario(Long id);

}
