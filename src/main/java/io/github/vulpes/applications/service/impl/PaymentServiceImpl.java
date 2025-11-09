package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.PaymentDTO;
import io.github.vulpes.applications.service.PaymentService;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.Pagamento;
import io.github.vulpes.domain.models.MonthlyPaymentStatus;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.SubscriberRepository;
import io.github.vulpes.infrastructure.jpa.PaymentRepository;
import io.github.vulpes.infrastructure.jpa.MonthlyPaymentStatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.format.TextStyle.FULL;
import static java.util.Locale.getDefault;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MonthlyPaymentStatusRepository monthlyPaymentStatusRepository;
    private final SubscriberRepository subscriberRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public PaymentServiceImpl(PaymentRepository paymentRepository, MonthlyPaymentStatusRepository monthlyPaymentStatusRepository, SubscriberRepository subscriberRepository) {
        this.paymentRepository = paymentRepository;
        this.monthlyPaymentStatusRepository = monthlyPaymentStatusRepository;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public PaymentDTO registerPayment(PaymentDTO dto) {
        Assinante subscriber = getSubscriber(dto.getSubscriberId());

        Pagamento payment = Pagamento.builder()
                .assinante(subscriber)
                .valorPago(dto.getAmountPaid())
                .dataPagamento(dto.getPaymentDate())
                .mesesCobertos(dto.getMonthsCovered())
                .cadastradoEm(LocalDateTime.now())
                .build();

        Pagamento savedPayment = paymentRepository.save(payment);

        updateMonthlyPaymentStatus(dto, savedPayment, subscriber);


        return modelMapper.map(savedPayment, PaymentDTO.class);
    }

    @Override
    public PaymentDTO updatePayment(Long paymentId, PaymentDTO dto) {
        Pagamento payment = getPaymentEntity(paymentId);
        payment.setValorPago(dto.getAmountPaid());
        payment.setDataPagamento(dto.getPaymentDate());
        payment.setMesesCobertos(dto.getMonthsCovered());
        payment.setAtualizadoEm(LocalDateTime.now());

        Pagamento savedPayment = paymentRepository.save(payment);

        updateMonthlyPaymentStatus(dto, savedPayment, payment.getAssinante());

        return modelMapper.map(savedPayment, PaymentDTO.class);
    }

    @Override
    public PaymentDTO getPayment(Long id) {
        Pagamento payment = getPaymentEntity(id);
        return modelMapper.map(payment, PaymentDTO.class);
    }

    @Override
    public Page<PaymentDTO> listPayments(String subscriberName, Pageable pageable) {
        Page<Pagamento> payments = paymentRepository.findPagamentos(subscriberName, pageable);
        return payments.map(payment -> modelMapper.map(payment, PaymentDTO.class));
    }

    @Override
    public Page<PaymentDTO> listPaymentsBySubscriber(Long subscriberId, Pageable pageable) {
        Page<Pagamento> payments = paymentRepository.findPagamentosAssinante(subscriberId, pageable);
        return payments.map(payment -> {
            PaymentDTO dto = modelMapper.map(payment, PaymentDTO.class);

            // Fetching paid months
            List<Integer> paidMonths = monthlyPaymentStatusRepository.findMesesByAssinanteId(subscriberId, payment.getId());

            // Converting to month names
            List<String> monthNames = paidMonths.stream()
                    .map(month -> Month.of(month).getDisplayName(FULL, getDefault()).toUpperCase())
                    .collect(Collectors.toList());

            dto.setMonths(monthNames);

            return dto;
        });
    }

    private void updateMonthlyPaymentStatus(PaymentDTO dto, Pagamento payment, Assinante subscriber) {
        IntStream.range(1, dto.getMonthsCovered() + 1).forEach(i -> {
            MonthlyPaymentStatus monthlyPaymentStatus = new MonthlyPaymentStatus();
            LocalDateTime coveredDate = dto.getPaymentDate().plusMonths(i - 1);

            monthlyPaymentStatus.setAssinante(subscriber);
            monthlyPaymentStatus.setPagamento(payment);
            monthlyPaymentStatus.setMes(coveredDate.getMonthValue());
            monthlyPaymentStatus.setAno(coveredDate.getYear());
            monthlyPaymentStatus.setStatusPagamento("PAGO");
            monthlyPaymentStatus.setCadastradoEm(LocalDateTime.now());

            monthlyPaymentStatusRepository.save(monthlyPaymentStatus);
        });


    }

    private Pagamento getPaymentEntity(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Payment not found"));
    }

    private Assinante getSubscriber(Long id) {
        return subscriberRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Subscriber not found"));
    }
}
