package io.github.vulpes.domain.models;

import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "payments", schema = "vulpes")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Assinante assinante;

    @Column(name = "amount_paid", nullable = false)
    private BigDecimal valorPago;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime dataPagamento;

    @Column(name = "months_covered", nullable = false)
    private Integer mesesCobertos;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime cadastradoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    @Column(name = "deleted_at")
    private LocalDateTime excluidoEm;

}

