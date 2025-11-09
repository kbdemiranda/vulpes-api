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
@Table(name = "subscribers", schema = "vulpes")
@SQLDelete(sql = "UPDATE vulpes.subscribers SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Assinante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime cadastradoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    @Column(name = "deleted_at")
    private LocalDateTime excluidoEm;
}

