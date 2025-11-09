package io.github.vulpes.domain.models;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "monthly_payment_status", schema = "vulpes")
public class MonthlyPaymentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Assinante assinante;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Pagamento pagamento;

    @Column(name = "month", nullable = false)
    private Integer mes;

    @Column(name = "year", nullable = false)
    private Integer ano;

    @Column(name = "payment_status", nullable = false)
    private String statusPagamento;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime cadastradoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    @Column(name = "deleted_at")
    private LocalDateTime excluidoEm;
}

