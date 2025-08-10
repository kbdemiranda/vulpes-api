package io.github.vulpes.applications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vulpes.applications.dto.PlataformaDTO;
import io.github.vulpes.domain.enums.TipoServico;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.jpa.PlataformaRepository;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PlataformaControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PlataformaRepository plataformaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Plataforma testePlataforma;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        setupTestData();
    }

    private void setupTestData() {
        plataformaRepository.deleteAll();
        
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
    @DisplayName("Should successfully list all platforms")
    void testListarPlataformas_Success() throws Exception {
        mockMvc.perform(get("/plataformas")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(testePlataforma.getId().intValue())))
                .andExpect(jsonPath("$.content[0].nome", is("Netflix")))
                .andExpect(jsonPath("$.content[0].preco", is(29.90)))
                .andExpect(jsonPath("$.content[0].url", is("https://netflix.com")))
                .andExpect(jsonPath("$.content[0].tipo_servico", is("STREAMING_VIDEO")))
                .andExpect(jsonPath("$.content[0].total_vagas", is(5)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @DisplayName("Should successfully list platforms with filter")
    void testListarPlataformas_WithFilter() throws Exception {
        mockMvc.perform(get("/plataformas")
                .param("nome", "Netflix")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Netflix")));

        mockMvc.perform(get("/plataformas")
                .param("nome", "Prime")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Should successfully find platform by ID")
    void testBuscarPlataforma_Success() throws Exception {
        mockMvc.perform(get("/plataformas/{id}", testePlataforma.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testePlataforma.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("Netflix")))
                .andExpect(jsonPath("$.preco", is(29.90)))
                .andExpect(jsonPath("$.tipo_servico", is("STREAMING_VIDEO")));
    }

    @Test
    @DisplayName("Should return 404 when platform not found")
    void testBuscarPlataforma_NotFound() throws Exception {
        mockMvc.perform(get("/plataformas/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should successfully create new platform")
    @Transactional
    void testCadastrarPlataforma_Success() throws Exception {
        PlataformaDTO novaPlataforma = new PlataformaDTO();
        novaPlataforma.setNome("Prime Video");
        novaPlataforma.setPreco(new BigDecimal("14.90"));
        novaPlataforma.setUrl("https://primevideo.com");
        novaPlataforma.setTipoServico(TipoServico.STREAMING_VIDEO);
        novaPlataforma.setTotalVagas(3);

        mockMvc.perform(post("/plataformas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novaPlataforma)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome", is("Prime Video")))
                .andExpect(jsonPath("$.preco", is(14.90)))
                .andExpect(jsonPath("$.url", is("https://primevideo.com")))
                .andExpect(jsonPath("$.tipo_servico", is("STREAMING_VIDEO")))
                .andExpect(jsonPath("$.total_vagas", is(3)))
                .andExpect(jsonPath("$.id", notNullValue()));

        // Verify platform was actually saved in database
        assertEquals(2, plataformaRepository.count());
        assertTrue(plataformaRepository.findAll().stream()
                .anyMatch(p -> "Prime Video".equals(p.getNome())));
    }

    @Test
    @DisplayName("Should successfully update existing platform")
    @Transactional
    void testAtualizarPlataforma_Success() throws Exception {
        PlataformaDTO updatedDto = new PlataformaDTO();
        updatedDto.setNome("Netflix Premium");
        updatedDto.setPreco(new BigDecimal("39.90"));
        updatedDto.setUrl("https://netflix.com/premium");
        updatedDto.setTipoServico(TipoServico.STREAMING_VIDEO);
        updatedDto.setTotalVagas(10);

        mockMvc.perform(put("/plataformas/{id}", testePlataforma.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Netflix Premium")))
                .andExpect(jsonPath("$.preco", is(39.90)))
                .andExpect(jsonPath("$.total_vagas", is(10)));

        // Verify platform was actually updated in database
        Plataforma updated = plataformaRepository.findById(testePlataforma.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Netflix Premium", updated.getNome());
        assertEquals(new BigDecimal("39.90"), updated.getPreco());
        assertEquals(10, updated.getTotalVagas());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent platform")
    void testAtualizarPlataforma_NotFound() throws Exception {
        PlataformaDTO updatedDto = new PlataformaDTO();
        updatedDto.setNome("Non-existent");
        updatedDto.setPreco(BigDecimal.TEN);
        updatedDto.setTipoServico(TipoServico.STREAMING_VIDEO);
        updatedDto.setTotalVagas(5);

        mockMvc.perform(put("/plataformas/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should successfully delete existing platform")
    @Transactional
    void testExcluirPlataforma_Success() throws Exception {
        mockMvc.perform(delete("/plataformas/{id}", testePlataforma.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify platform was actually deleted from database
        assertEquals(0, plataformaRepository.count());
        assertFalse(plataformaRepository.existsById(testePlataforma.getId()));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent platform")
    void testExcluirPlataforma_NotFound() throws Exception {
        mockMvc.perform(delete("/plataformas/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify existing platform is still there
        assertEquals(1, plataformaRepository.count());
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void testListarPlataformas_Pagination() throws Exception {
        // Create additional platforms for pagination test
        for (int i = 1; i <= 15; i++) {
            Plataforma plataforma = Plataforma.builder()
                    .nome("Plataforma " + i)
                    .preco(new BigDecimal("10.00"))
                    .url("https://plataforma" + i + ".com")
                    .tipoServico(TipoServico.STREAMING_VIDEO)
                    .totalVagas(5)
                    .vagasDisponiveis(5)
                    .cadastradoEm(LocalDateTime.now())
                    .build();
            plataformaRepository.save(plataforma);
        }

        // Test first page
        mockMvc.perform(get("/plataformas")
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
        mockMvc.perform(get("/plataformas")
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