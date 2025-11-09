package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentDTO registerPayment(PaymentDTO dto);
    PaymentDTO updatePayment(Long paymentId, PaymentDTO dto);
    PaymentDTO getPayment(Long id);
    Page<PaymentDTO> listPayments(String subscriberName, Pageable pageable);
    Page<PaymentDTO> listPaymentsBySubscriber(Long subscriberId, Pageable pageable);
}
