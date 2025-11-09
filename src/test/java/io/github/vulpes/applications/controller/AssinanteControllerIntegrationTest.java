package io.github.vulpes.applications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vulpes.applications.dto.SubscriberDTO;
import io.github.vulpes.applications.dto.AssociatePlatformsDTO;
import io.github.vulpes.domain.enums.TipoServico;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.jpa.SubscriberRepository;
import io.github.vulpes.infrastructure.jpa.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AssinanteControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SubscriberRepository assinanteRepository;

    @Autowired
    private PlatformRepository plataformaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Assinante testeAssinante;
    private Plataforma testePlataforma;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        setupTestData();
    }

    private void setupTestData() {
        assinanteRepository.deleteAll();
        plataformaRepository.deleteAll();
        
        testeAssinante = Assinante.builder()
                .nome("João Silva")
                .email("joao.silva@example.com")
                .cadastradoEm(LocalDateTime.now())
                .build();
        
        testeAssinante = assinanteRepository.save(testeAssinante);

        // Create a test platform for association tests
        testePlataforma = Plataforma.builder()
                .nome("Netflix")
                .preco(new BigDecimal("29.90"))
                .url("https://netflix.com")
                .tipoServico(TipoServico.STREAMING_VIDEO)
                .totalVagas(5)
                .vagasDisponiveis(5)
                .cadastradoEm(LocalDateTime.now())
                .build();
        
        testePlataforma = plataformaRepository.save(testePlataforma);
    }

    @Test
    @DisplayName("Should successfully list all assinantes")
    void testListarAssinantes_Success() throws Exception {
        mockMvc.perform(get("/assinantes")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(testeAssinante.getId().intValue())))
                .andExpect(jsonPath("$.content[0].nome", is("João Silva")))
                .andExpect(jsonPath("$.content[0].email", is("joao.silva@example.com")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @DisplayName("Should successfully list assinantes with filter")
    void testListarAssinantes_WithFilter() throws Exception {
        mockMvc.perform(get("/assinantes")
                .param("nome", "João")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("João Silva")));

        mockMvc.perform(get("/assinantes")
                .param("nome", "Maria")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Should successfully find assinante by ID")
    void testBuscarAssinante_Success() throws Exception {
        mockMvc.perform(get("/assinantes/{id}", testeAssinante.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testeAssinante.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("João Silva")))
                .andExpect(jsonPath("$.email", is("joao.silva@example.com")));
    }

    @Test
    @DisplayName("Should return 404 when assinante not found")
    void testBuscarAssinante_NotFound() throws Exception {
        mockMvc.perform(get("/assinantes/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should successfully create new assinante")
    @Transactional
    void testCadastrarAssinante_Success() throws Exception {
        SubscriberDTO novoAssinante = new SubscriberDTO();
        novoAssinante.setNome("Maria Santos");
        novoAssinante.setEmail("maria.santos@example.com");

        mockMvc.perform(post("/assinantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoAssinante)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome", is("Maria Santos")))
                .andExpect(jsonPath("$.email", is("maria.santos@example.com")))
                .andExpect(jsonPath("$.id", notNullValue()));

        // Verify assinante was actually saved in database
        assertEquals(2, assinanteRepository.count());
        assertTrue(assinanteRepository.findAll().stream()
                .anyMatch(a -> "Maria Santos".equals(a.getNome())));
    }

    @Test
    @DisplayName("Should successfully update existing assinante")
    @Transactional
    void testAtualizarAssinante_Success() throws Exception {
        SubscriberDTO updatedDto = new SubscriberDTO();
        updatedDto.setNome("João Silva Updated");
        updatedDto.setEmail("joao.silva.updated@example.com");

        mockMvc.perform(put("/assinantes/{id}", testeAssinante.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("João Silva Updated")))
                .andExpect(jsonPath("$.email", is("joao.silva.updated@example.com")));

        // Verify assinante was actually updated in database
        Assinante updated = assinanteRepository.findById(testeAssinante.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("João Silva Updated", updated.getNome());
        assertEquals("joao.silva.updated@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent assinante")
    void testAtualizarAssinante_NotFound() throws Exception {
        SubscriberDTO updatedDto = new SubscriberDTO();
        updatedDto.setNome("Non-existent");
        updatedDto.setEmail("nonexistent@example.com");

        mockMvc.perform(put("/assinantes/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should successfully delete existing assinante")
    @Transactional
    void testExcluirAssinante_Success() throws Exception {
        mockMvc.perform(delete("/assinantes/{id}", testeAssinante.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify assinante count decreases (soft delete affects query results)
        // Note: The repository's findAssinante query excludes soft-deleted records
        assertEquals(0, assinanteRepository.findAssinante(null, 
                org.springframework.data.domain.PageRequest.of(0, 10)).getTotalElements());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent assinante")
    void testExcluirAssinante_NotFound() throws Exception {
        mockMvc.perform(delete("/assinantes/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify existing assinante is still there
        assertEquals(1, assinanteRepository.count());
    }

    @Test
    @DisplayName("Should successfully associate plataformas to assinante")
    @Transactional
    void testAssociarPlataformas_Success() throws Exception {
        AssociatePlatformsDTO associarDto = new AssociatePlatformsDTO();
        associarDto.setPlataformaIds(Arrays.asList(testePlataforma.getId()));

        mockMvc.perform(post("/assinantes/{id}/associar-plataformas", testeAssinante.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associarDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Plataformas associadas com sucesso!")));
    }

    @Test
    @DisplayName("Should return 400 when associating invalid plataforma")
    void testAssociarPlataformas_InvalidPlataforma() throws Exception {
        AssociatePlatformsDTO associarDto = new AssociatePlatformsDTO();
        associarDto.setPlataformaIds(Arrays.asList(999L));

        mockMvc.perform(post("/assinantes/{id}/associar-plataformas", testeAssinante.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associarDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully desassociate plataforma from assinante")
    @Transactional
    void testDesassociarPlataforma_Success() throws Exception {
        // First associate a plataforma
        AssociatePlatformsDTO associarDto = new AssociatePlatformsDTO();
        associarDto.setPlataformaIds(Arrays.asList(testePlataforma.getId()));
        
        mockMvc.perform(post("/assinantes/{id}/associar-plataformas", testeAssinante.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associarDto)))
                .andExpect(status().isOk());

        // Then desassociate it
        mockMvc.perform(delete("/assinantes/{assinanteId}/desassociar-plataforma/{plataformaId}", 
                testeAssinante.getId(), testePlataforma.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Plataforma desassociada com sucesso!")));
    }

    @Test
    @DisplayName("Should successfully handle desassociating non-associated plataforma")
    void testDesassociarPlataforma_NotAssociated() throws Exception {
        // Note: The current implementation doesn't validate if association exists
        // It just performs a soft delete on any matching records (even if none exist)
        mockMvc.perform(delete("/assinantes/{assinanteId}/desassociar-plataforma/{plataformaId}", 
                testeAssinante.getId(), testePlataforma.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Plataforma desassociada com sucesso!")));
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void testListarAssinantes_Pagination() throws Exception {
        // Create additional assinantes for pagination test
        for (int i = 1; i <= 15; i++) {
            Assinante assinante = Assinante.builder()
                    .nome("Assinante " + i)
                    .email("assinante" + i + "@example.com")
                    .cadastradoEm(LocalDateTime.now())
                    .build();
            assinanteRepository.save(assinante);
        }

        // Test first page
        mockMvc.perform(get("/assinantes")
                .param("pagina", "0")
                .param("quantidade", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements", is(16)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)));

        // Test second page
        mockMvc.perform(get("/assinantes")
                .param("pagina", "1")
                .param("quantidade", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(6)))
                .andExpect(jsonPath("$.first", is(false)))
                .andExpect(jsonPath("$.last", is(true)));
    }
}