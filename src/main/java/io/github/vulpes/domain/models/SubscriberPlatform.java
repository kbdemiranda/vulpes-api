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
@Table(name = "subscribers_platforms", schema = "vulpes")
public class SubscriberPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Assinante assinante;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    private Plataforma plataforma;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime cadastradoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    @Column(name = "deleted_at")
    private LocalDateTime excluidoEm;
}

