package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.UsuarioDTO;
import io.github.vulpes.applications.service.impl.UsuarioServiceImpl;
import io.github.vulpes.domain.models.Perfil;
import io.github.vulpes.domain.models.Usuario;
import io.github.vulpes.infrastructure.jpa.PerfilRepository;
import io.github.vulpes.infrastructure.jpa.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PerfilRepository perfilRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, perfilRepository);
    }

    @Test
    void testCadastrarUsuario() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("John");
        dto.setSobrenome("Doe");
        dto.setEmail("john@doe.com");
        dto.setSenha("123");
        dto.setPerfisId(Collections.singletonList(1L));

        Perfil perfil = new Perfil();
        perfil.setId(1L);

        when(perfilRepository.findAllById(dto.getPerfisId())).thenReturn(Collections.singletonList(perfil));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioDTO result = usuarioService.cadastrarUsuario(dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();

        assertTrue(passwordEncoder.matches("123", saved.getSenha()));
        assertEquals(dto.getNome(), result.getNome());
    }

    @Test
    void testBuscarUsuarioPorId() {
        Usuario usuario = Usuario.builder().id(1L).nome("John").sobrenome("Doe").email("john@doe.com").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDTO dto = usuarioService.buscarUsuarioPorId(1L);
        assertEquals(usuario.getNome(), dto.getNome());
    }

    @Test
    void testAtualizarUsuario() {
        Usuario usuario = Usuario.builder().id(1L).nome("John").sobrenome("Doe").email("old@doe.com").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("Novo");
        dto.setSobrenome("Nome");
        dto.setEmail("novo@teste.com");
        dto.setPerfisId(Collections.singletonList(1L));
        dto.setSenha("123");

        UsuarioDTO result = usuarioService.atualizarUsuario(1L, dto);

        assertEquals(dto.getNome(), result.getNome());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testAtualizarSenha() {
        Usuario usuario = Usuario.builder().id(1L).senha("antiga").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.atualizarSenha(1L, "nova");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();

        assertTrue(passwordEncoder.matches("nova", saved.getSenha()));
    }

    @Test
    void testAtualizarPerfis() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Perfil perfil = new Perfil();
        perfil.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(perfilRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(perfil));

        usuarioService.atualizarPerfis(1L, Arrays.asList(1L));

        verify(usuarioRepository).save(usuario);
        assertEquals(1, usuario.getPerfis().size());
    }

    @Test
    void testExcluirUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.excluirUsuario(1L);

        verify(usuarioRepository).deleteUsuario(1L);
    }
}
