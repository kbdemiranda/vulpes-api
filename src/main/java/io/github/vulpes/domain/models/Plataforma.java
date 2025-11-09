package io.github.vulpes.domain.models;

import io.github.vulpes.domain.enums.TipoServico;
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
@Table(name = "platforms", schema = "vulpes")
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String nome;

    @Column(name = "price",nullable = false)
    private BigDecimal preco;

    @Column(name = "url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private TipoServico tipoServico;

    @Column(name = "total_slots",nullable = false)
    private Integer totalVagas;

    @Column(name = "available_slots", nullable = false)
    private Integer vagasDisponiveis;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime cadastradoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    @Column(name = "deleted_at")
    private LocalDateTime excluidoEm;

}

