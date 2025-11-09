package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PaymentDTO;
import io.github.vulpes.applications.service.impl.PaymentServiceImpl;
import io.github.vulpes.domain.models.Subscriber;
import io.github.vulpes.domain.models.Payment;
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

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private MonthlyPaymentStatusRepository monthlyPaymentStatusRepository;
    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentRepository, monthlyPaymentStatusRepository, subscriberRepository);
    }

    @Test
    void testRegisterPayment() {
        PaymentDTO dto = new PaymentDTO();
        dto.setSubscriberId(1L);
        dto.setPaidAmount(BigDecimal.TEN);
        dto.setCoveredMonths(2);
        dto.setPaymentDate(LocalDateTime.now());

        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentDTO result = paymentService.registerPayment(dto);

        verify(paymentRepository).save(any(Payment.class));
        verify(monthlyPaymentStatusRepository, times(2)).save(any(MonthlyPaymentStatus.class));
        assertEquals(1L, result.getId());
        assertEquals(dto.getPaidAmount(), result.getPaidAmount());
    }

    @Test
    void testUpdatePayment() {
        Payment payment = Payment.builder().id(1L).subscriber(new Subscriber()).paidAmount(BigDecimal.ONE).coveredMonths(1).paymentDate(LocalDateTime.now()).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentDTO dto = new PaymentDTO();
        dto.setPaidAmount(BigDecimal.TEN);
        dto.setCoveredMonths(2);
        dto.setPaymentDate(LocalDateTime.now());

        PaymentDTO result = paymentService.updatePayment(1L, dto);

        verify(monthlyPaymentStatusRepository, times(2)).save(any(MonthlyPaymentStatus.class));
        assertEquals(dto.getPaidAmount(), result.getPaidAmount());
    }

    @Test
    void testFindPayment() {
        Payment payment = Payment.builder().id(1L).paidAmount(BigDecimal.TEN).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentDTO dto = paymentService.findPayment(1L);

        assertEquals(payment.getId(), dto.getId());
        assertEquals(payment.getPaidAmount(), dto.getPaidAmount());
    }

    @Test
    void testListPayments() {
        Payment p = Payment.builder().id(1L).paidAmount(BigDecimal.TEN).subscriber(new Subscriber()).build();
        Page<Payment> page = new PageImpl<>(Collections.singletonList(p));
        when(paymentRepository.findPayments("", PageRequest.of(0, 10))).thenReturn(page);

        Page<PaymentDTO> expected = page.map(payment -> modelMapper.map(payment, PaymentDTO.class));
        Page<PaymentDTO> result = paymentService.listPayments("", PageRequest.of(0, 10));

        assertEquals(expected, result);
    }

    @Test
    void testListSubscriberPayments() {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        Payment payment = Payment.builder().id(1L).subscriber(subscriber).paidAmount(BigDecimal.TEN).coveredMonths(2).paymentDate(LocalDateTime.now()).build();
        Page<Payment> page = new PageImpl<>(Collections.singletonList(payment));

        when(paymentRepository.findSubscriberPayments(1L, PageRequest.of(0, 10))).thenReturn(page);
        when(monthlyPaymentStatusRepository.findMonthsBySubscriberId(1L, 1L)).thenReturn(Arrays.asList(1, 2));

        Page<PaymentDTO> result = paymentService.listSubscriberPayments(1L, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getContent().get(0).getMonths().size());
    }
}
