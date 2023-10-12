package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.UsuarioDTO;
import io.github.vulpes.applications.service.UsuarioService;
import io.github.vulpes.domain.models.Usuario;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public UsuarioDTO cadastrarUsuario(UsuarioDTO dto) {
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .sobrenome(dto.getSobrenome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .cadastradoEm(LocalDateTime.now())
                .build();

        Usuario u = usuarioRepository.save(usuario);

        return new UsuarioDTO(u);
    }

    @Override
    public UsuarioDTO buscarUsuarioPorId(Long id) {
        return null;
    }

    @Override
    public UsuarioDTO atualizarUsuario(Long id, UsuarioDTO dto) {
        return null;
    }

    @Override
    public void excluirUsuario(Long id) {

    }

    private Usuario getUsuario(String email){
        return usuarioRepository.
                findByEmail(email)
                .orElseThrow(() -> new VulpesException(404, "Usuário não encontrado"));
    }

    private Usuario getUsuario(Long id){
        return usuarioRepository
                .findById(id)
                .orElseThrow(() -> new VulpesException(404, "Usuário não encontrado"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUsuario(username);
    }
}
