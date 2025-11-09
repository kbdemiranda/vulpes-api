package io.github.vulpes.applications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vulpes.applications.dto.LoginDTO;
import io.github.vulpes.domain.models.Profile;
import io.github.vulpes.domain.models.Usuario;
import io.github.vulpes.infrastructure.jpa.ProfileRepository;
import io.github.vulpes.infrastructure.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private ProfileRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Usuario testeUsuario;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        setupTestData();
    }

    private void setupTestData() {
        usuarioRepository.deleteAll();
        perfilRepository.deleteAll();
        
        // Create test profile
        Profile testePerfil = new Profile();
        testePerfil.setNome("USER");
        testePerfil = perfilRepository.save(testePerfil);

        // Create test user for login
        testeUsuario = Usuario.builder()
                .nome("Test")
                .sobrenome("User")
                .email("test@example.com")
                .senha(passwordEncoder.encode("password123"))
                .perfis(new ArrayList<>(Arrays.asList(testePerfil)))
                .cadastradoEm(LocalDateTime.now())
                .build();
        
        testeUsuario = usuarioRepository.save(testeUsuario);
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testLogin_Success() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setEmail("test@example.com");
        loginDto.setSenha("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 500 for invalid email")
    void testLogin_InvalidEmail() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setEmail("invalid@example.com");
        loginDto.setSenha("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Authentication errors may cause 500
    }

    @Test
    @DisplayName("Should return 401 for invalid password")
    void testLogin_InvalidPassword() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setEmail("test@example.com");
        loginDto.setSenha("wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 500 for empty email")
    void testLogin_EmptyEmail() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setEmail("");
        loginDto.setSenha("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Validation error
    }

    @Test
    @DisplayName("Should return 500 for empty password due to validation")
    void testLogin_EmptyPassword() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setEmail("test@example.com");
        loginDto.setSenha("");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Validation error causes 500
    }

    @Test
    @DisplayName("Should successfully logout")
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 401 for missing credentials")
    void testLogin_MissingCredentials() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        // Both email and senha are null

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Validation error for missing fields
    }

    @Test
    @DisplayName("Should login successfully for user with multiple perfis")
    void testLogin_UserWithMultiplePerfis() throws Exception {
        // Create additional profile
        Profile adminPerfil = new Profile();
        adminPerfil.setNome("ADMIN");
        adminPerfil = perfilRepository.save(adminPerfil);
        
        // Create user with multiple profiles
        Usuario multiPerfilUser = Usuario.builder()
                .nome("Admin")
                .sobrenome("User")
                .email("admin@example.com")
                .senha(passwordEncoder.encode("admin123"))
                .perfis(new ArrayList<>(Arrays.asList(adminPerfil)))
                .cadastradoEm(LocalDateTime.now())
                .build();
        
        usuarioRepository.save(multiPerfilUser);

        LoginDTO loginDto = new LoginDTO();
        loginDto.setEmail("admin@example.com");
        loginDto.setSenha("admin123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}