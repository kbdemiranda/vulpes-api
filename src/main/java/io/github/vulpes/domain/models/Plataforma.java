package io.github.vulpes.domain.models;

import io.github.vulpes.domain.enums.TipoServico;
import io.github.vulpes.domain.enums.CicloCobranca;
import io.github.vulpes.domain.converters.MonthDayAttributeConverter;
import lombok.*;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.MonthDay;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "plataformas", schema = "vulpes")
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "preco",nullable = false)
    private BigDecimal preco;

    @Column(name = "url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servico", nullable = false)
    private TipoServico tipoServico;

    @Column(name = "total_vagas",nullable = false)
    private Integer totalVagas;

    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;

    @Column(name = "cadastrado_em", nullable = false)
    private LocalDateTime cadastradoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "ciclo_cobranca", nullable = false)
    private CicloCobranca cicloCobranca = CicloCobranca.MENSAL;

    @Convert(converter = MonthDayAttributeConverter.class)
    @Column(name = "dia_mes_cobranca")
    private MonthDay diaMesCobranca;

}
