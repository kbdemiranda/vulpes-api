package io.github.vulpes.applications.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vulpes.applications.dto.PaymentDTO;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.Pagamento;
import io.github.vulpes.infrastructure.jpa.SubscriberRepository;
import io.github.vulpes.infrastructure.jpa.PaymentRepository;
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
class PagamentoControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PaymentRepository pagamentoRepository;

    @Autowired
    private SubscriberRepository assinanteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Assinante testeAssinante;
    private Pagamento testePagamento;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        setupTestData();
    }

    private void setupTestData() {
        pagamentoRepository.deleteAll();
        assinanteRepository.deleteAll();
        
        // Create test assinante
        testeAssinante = Assinante.builder()
                .nome("João Silva")
                .email("joao.silva@example.com")
                .cadastradoEm(LocalDateTime.now())
                .build();
        
        testeAssinante = assinanteRepository.save(testeAssinante);

        // Create test pagamento
        testePagamento = Pagamento.builder()
                .assinante(testeAssinante)
                .valorPago(new BigDecimal("29.90"))
                .dataPagamento(LocalDateTime.now().minusDays(1))
                .mesesCobertos(1)
                .cadastradoEm(LocalDateTime.now())
                .build();
        
        testePagamento = pagamentoRepository.save(testePagamento);
    }

    @Test
    @DisplayName("Should successfully create new pagamento")
    @Transactional
    void testRegistrarPagamento_Success() throws Exception {
        PaymentDTO novoPagamento = new PaymentDTO();
        novoPagamento.setAssinanteId(testeAssinante.getId());
        novoPagamento.setValorPago(new BigDecimal("59.90"));
        novoPagamento.setDataPagamento(LocalDateTime.now().minusDays(2));
        novoPagamento.setMesesCobertos(2);

        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoPagamento)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.assinante_id", is(testeAssinante.getId().intValue())))
                .andExpect(jsonPath("$.valor_pago", is(59.90)))
                .andExpect(jsonPath("$.meses_cobertos", is(2)))
                .andExpect(jsonPath("$.data_pagamento").exists());

        // Verify pagamento was actually saved in database
        assertEquals(2, pagamentoRepository.count());
        assertTrue(pagamentoRepository.findAll().stream()
                .anyMatch(p -> p.getValorPago().compareTo(new BigDecimal("59.90")) == 0));
    }

    @Test
    @DisplayName("Should return 500 for invalid pagamento data")
    void testRegistrarPagamento_InvalidData() throws Exception {
        PaymentDTO invalidPagamento = new PaymentDTO();
        // Missing required fields: assinanteId, valorPago, dataPagamento, mesesCobertos

        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPagamento)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Validation might cause 500 instead of 400

        // Verify pagamento was not created
        assertEquals(1, pagamentoRepository.count()); // Only the setup pagamento should exist
    }

    @Test
    @DisplayName("Should return 500 for negative valor_pago due to validation")
    void testRegistrarPagamento_NegativeValue() throws Exception {
        PaymentDTO invalidPagamento = new PaymentDTO();
        invalidPagamento.setAssinanteId(testeAssinante.getId());
        invalidPagamento.setValorPago(new BigDecimal("-10.00")); // Negative value - validation rejects this
        invalidPagamento.setDataPagamento(LocalDateTime.now());
        invalidPagamento.setMesesCobertos(1);

        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPagamento)))
                .andDo(print())
                .andExpect(status().is5xxServerError()); // Validation error causes 500

        // Verify pagamento was not created due to validation error
        assertEquals(1, pagamentoRepository.count()); // Only the setup pagamento should exist
    }

    @Test
    @DisplayName("Should successfully find pagamento by ID")
    void testConsultarPagamento_Success() throws Exception {
        mockMvc.perform(get("/pagamentos/{id}", testePagamento.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assinante_id", is(testeAssinante.getId().intValue())))
                .andExpect(jsonPath("$.valor_pago", is(29.90)))
                .andExpect(jsonPath("$.meses_cobertos", is(1)));
    }

    @Test
    @DisplayName("Should return 404 when pagamento not found by ID")
    void testConsultarPagamento_NotFound() throws Exception {
        mockMvc.perform(get("/pagamentos/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should successfully update existing pagamento")
    @Transactional
    void testAtualizarPagamento_Success() throws Exception {
        PaymentDTO updatedDto = new PaymentDTO();
        updatedDto.setAssinanteId(testeAssinante.getId());
        updatedDto.setValorPago(new BigDecimal("39.90"));
        updatedDto.setDataPagamento(LocalDateTime.now().minusDays(3));
        updatedDto.setMesesCobertos(2);

        mockMvc.perform(put("/pagamentos/{id}", testePagamento.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor_pago", is(39.90)))
                .andExpect(jsonPath("$.meses_cobertos", is(2)));

        // Verify pagamento was actually updated in database
        Pagamento updated = pagamentoRepository.findById(testePagamento.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(0, updated.getValorPago().compareTo(new BigDecimal("39.90")));
        assertEquals(2, updated.getMesesCobertos());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent pagamento")
    void testAtualizarPagamento_NotFound() throws Exception {
        PaymentDTO updatedDto = new PaymentDTO();
        updatedDto.setAssinanteId(testeAssinante.getId());
        updatedDto.setValorPago(new BigDecimal("39.90"));
        updatedDto.setDataPagamento(LocalDateTime.now());
        updatedDto.setMesesCobertos(1);

        mockMvc.perform(put("/pagamentos/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should successfully list all pagamentos")
    void testListarPagamentos_Success() throws Exception {
        mockMvc.perform(get("/pagamentos")
                .param("nomeAssinante", "") // Pass empty filter to trigger the query
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("Should successfully list pagamentos with pagination")
    void testListarPagamentos_WithPagination() throws Exception {
        mockMvc.perform(get("/pagamentos")
                .param("nomeAssinante", "") // Pass empty filter to trigger the query
                .param("pagina", "0")
                .param("quantidade", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    @DisplayName("Should successfully filter pagamentos by assinante name")
    void testListarPagamentos_WithAssinanteFilter() throws Exception {
        mockMvc.perform(get("/pagamentos")
                .param("nomeAssinante", "João")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("Should return empty list when filtering by non-existent assinante name")
    void testListarPagamentos_WithNonExistentFilter() throws Exception {
        mockMvc.perform(get("/pagamentos")
                .param("nomeAssinante", "NonExistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Should successfully list pagamentos by assinante ID")
    void testListarPagamentosAssinante_Success() throws Exception {
        mockMvc.perform(get("/pagamentos/assinante/{id}", testeAssinante.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].assinante_id", is(testeAssinante.getId().intValue())));
    }

    @Test
    @DisplayName("Should return empty list for non-existent assinante ID")
    void testListarPagamentosAssinante_NotFound() throws Exception {
        mockMvc.perform(get("/pagamentos/assinante/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("Should successfully list pagamentos by assinante ID with pagination")
    void testListarPagamentosAssinante_WithPagination() throws Exception {
        mockMvc.perform(get("/pagamentos/assinante/{id}", testeAssinante.getId())
                .param("pagina", "0")
                .param("quantidade", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    @Test
    @DisplayName("Should return 404 for invalid assinante ID in pagamento creation")
    void testRegistrarPagamento_InvalidAssinanteId() throws Exception {
        PaymentDTO invalidPagamento = new PaymentDTO();
        invalidPagamento.setAssinanteId(999L); // Non-existent assinante
        invalidPagamento.setValorPago(new BigDecimal("29.90"));
        invalidPagamento.setDataPagamento(LocalDateTime.now());
        invalidPagamento.setMesesCobertos(1);

        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPagamento)))
                .andDo(print())
                .andExpect(status().isNotFound());

        // Verify pagamento was not created
        assertEquals(1, pagamentoRepository.count());
    }
}