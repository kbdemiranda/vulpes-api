package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.UserDTO;
import io.github.vulpes.applications.service.impl.UserServiceImpl;
import io.github.vulpes.domain.models.Profile;
import io.github.vulpes.domain.models.Usuario;
import io.github.vulpes.infrastructure.jpa.ProfileRepository;
import io.github.vulpes.infrastructure.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UserRepository usuarioRepository;
    @Mock
    private ProfileRepository perfilRepository;

    @InjectMocks
    private UserServiceImpl usuarioService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UserServiceImpl(usuarioRepository, perfilRepository, passwordEncoder);
    }

    @Test
    void testCadastrarUsuario() {
        UserDTO dto = new UserDTO();
        dto.setNome("John");
        dto.setSobrenome("Doe");
        dto.setEmail("john@doe.com");
        dto.setSenha("123");
        dto.setPerfisId(Collections.singletonList(1L));

        Profile perfil = new Profile();
        perfil.setId(1L);

        when(perfilRepository.findAllById(dto.getPerfisId())).thenReturn(Collections.singletonList(perfil));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDTO result = usuarioService.cadastrarUsuario(dto);

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

        UserDTO dto = usuarioService.buscarUsuarioPorId(1L);
        assertEquals(usuario.getNome(), dto.getNome());
    }

    @Test
    void testAtualizarUsuario() {
        Usuario usuario = Usuario.builder().id(1L).nome("John").sobrenome("Doe").email("old@doe.com").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO dto = new UserDTO();
        dto.setNome("Novo");
        dto.setSobrenome("Nome");
        dto.setEmail("novo@teste.com");
        dto.setPerfisId(Collections.singletonList(1L));
        dto.setSenha("123");

        UserDTO result = usuarioService.atualizarUsuario(1L, dto);

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
        Profile perfil = new Profile();
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
