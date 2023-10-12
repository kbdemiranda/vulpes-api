package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.UsuarioDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsuarioService extends UserDetailsService {

    UsuarioDTO cadastrarUsuario(UsuarioDTO dto);
    UsuarioDTO buscarUsuarioPorId(Long id);
    UsuarioDTO atualizarUsuario(Long id, UsuarioDTO dto);
    void excluirUsuario(Long id);

}
