package io.github.vulpes.domain.models;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "assinantes", schema = "vulpes")
@SQLDelete(sql = "UPDATE vulpes.assinantes SET excluido_em = now() WHERE id = ?")
@Where(clause = "excluido_em IS NULL")
public class Assinante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "cadastrado_em", nullable = false)
    private LocalDateTime cadastradoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;
}

