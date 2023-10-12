package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.UsuarioDTO;
import io.github.vulpes.applications.service.UsuarioService;
import io.github.vulpes.domain.models.Perfil;
import io.github.vulpes.domain.models.Usuario;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.PerfilRepository;
import io.github.vulpes.infrastructure.jpa.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }


    @Override
    public UsuarioDTO cadastrarUsuario(UsuarioDTO dto) {
        List<Perfil> perfis = getPerfis(dto.getPerfisId());

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .sobrenome(dto.getSobrenome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .perfis(perfis)
                .cadastradoEm(LocalDateTime.now())
                .build();

        Usuario u = usuarioRepository.save(usuario);

        return new UsuarioDTO(u);
    }

    @Override
    public UsuarioDTO buscarUsuarioPorId(Long id) {
        Usuario usuario = getUsuario(id);
        return new UsuarioDTO(usuario);
    }

    @Override
    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = getUsuario(email);
        return new UsuarioDTO(usuario);
    }

    @Override
    public UsuarioDTO atualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = getUsuario(id);
        usuario.setNome(dto.getNome());
        usuario.setSobrenome(dto.getSobrenome());
        usuario.setEmail(dto.getEmail());
        usuario.setAtualizadoEm(LocalDateTime.now());

        Usuario u = usuarioRepository.save(usuario);
        return new UsuarioDTO(u);
    }

    @Override
    public void atualizarSenha(Long id, String senha) {
        Usuario usuario = getUsuario(id);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setAtualizadoEm(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    @Override
    public void atualizarPerfis(Long id, List<Long> perfis) {
        Usuario usuario = getUsuario(id);
        usuario.setPerfis(getPerfis(perfis));

        usuarioRepository.save(usuario);
    }

    @Override
    public void excluirUsuario(Long id) {
        Usuario usuario = getUsuario(id);
        usuarioRepository.deleteUsuario(usuario.getId());
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

    private List<Perfil> getPerfis(List<Long> perfisId){
        return perfilRepository.findAllById(perfisId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUsuario(username);
    }
}
