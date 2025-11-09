package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PaymentDTO;
import io.github.vulpes.applications.service.impl.PaymentServiceImpl;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.Pagamento;
import io.github.vulpes.domain.models.MonthlyPaymentStatus;
import io.github.vulpes.infrastructure.jpa.SubscriberRepository;
import io.github.vulpes.infrastructure.jpa.PaymentRepository;
import io.github.vulpes.infrastructure.jpa.MonthlyPaymentStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    @Mock
    private PaymentRepository pagamentoRepository;
    @Mock
    private MonthlyPaymentStatusRepository statusPagamentoMensalRepository;
    @Mock
    private SubscriberRepository assinanteRepository;

    @InjectMocks
    private PaymentServiceImpl pagamentoService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pagamentoService = new PaymentServiceImpl(pagamentoRepository, statusPagamentoMensalRepository, assinanteRepository);
    }

    @Test
    void testRegistrarPagamento() {
        PaymentDTO dto = new PaymentDTO();
        dto.setAssinanteId(1L);
        dto.setValorPago(BigDecimal.TEN);
        dto.setMesesCobertos(2);
        dto.setDataPagamento(LocalDateTime.now());

        Assinante assinante = new Assinante();
        assinante.setId(1L);
        when(assinanteRepository.findById(1L)).thenReturn(Optional.of(assinante));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> {
            Pagamento p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentDTO result = pagamentoService.registrarPagamento(dto);

        verify(pagamentoRepository).save(any(Pagamento.class));
        verify(statusPagamentoMensalRepository, times(2)).save(any(MonthlyPaymentStatus.class));
        assertEquals(1L, result.getId());
        assertEquals(dto.getValorPago(), result.getValorPago());
    }

    @Test
    void testAtualizarPagamento() {
        Pagamento pagamento = Pagamento.builder().id(1L).assinante(new Assinante()).valorPago(BigDecimal.ONE).mesesCobertos(1).dataPagamento(LocalDateTime.now()).build();
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentDTO dto = new PaymentDTO();
        dto.setValorPago(BigDecimal.TEN);
        dto.setMesesCobertos(2);
        dto.setDataPagamento(LocalDateTime.now());

        PaymentDTO result = pagamentoService.atualizarPagamento(1L, dto);

        verify(statusPagamentoMensalRepository, times(2)).save(any(MonthlyPaymentStatus.class));
        assertEquals(dto.getValorPago(), result.getValorPago());
    }

    @Test
    void testConsultarPagamento() {
        Pagamento pagamento = Pagamento.builder().id(1L).valorPago(BigDecimal.TEN).build();
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        PaymentDTO dto = pagamentoService.consultarPagamento(1L);

        assertEquals(pagamento.getId(), dto.getId());
        assertEquals(pagamento.getValorPago(), dto.getValorPago());
    }

    @Test
    void testListarPagamentos() {
        Pagamento p = Pagamento.builder().id(1L).valorPago(BigDecimal.TEN).assinante(new Assinante()).build();
        Page<Pagamento> page = new PageImpl<>(Collections.singletonList(p));
        when(pagamentoRepository.findPagamentos("", PageRequest.of(0, 10))).thenReturn(page);

        Page<PaymentDTO> expected = page.map(pg -> modelMapper.map(pg, PaymentDTO.class));
        Page<PaymentDTO> result = pagamentoService.listarPagamentos("", PageRequest.of(0, 10));

        assertEquals(expected, result);
    }

    @Test
    void testListarPagamentosAssinante() {
        Assinante assinante = new Assinante();
        assinante.setId(1L);
        Pagamento pagamento = Pagamento.builder().id(1L).assinante(assinante).valorPago(BigDecimal.TEN).mesesCobertos(2).dataPagamento(LocalDateTime.now()).build();
        Page<Pagamento> page = new PageImpl<>(Collections.singletonList(pagamento));

        when(pagamentoRepository.findPagamentosAssinante(1L, PageRequest.of(0, 10))).thenReturn(page);
        when(statusPagamentoMensalRepository.findMesesByAssinanteId(1L, 1L)).thenReturn(Arrays.asList(1, 2));

        Page<PaymentDTO> result = pagamentoService.listarPagamentosAssinante(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getContent().get(0).getMeses().size());
    }
}
