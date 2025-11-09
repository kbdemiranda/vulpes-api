package io.github.vulpes.domain.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "usuarios")
@Entity
@Table(name = "profiles", schema = "vulpes")
public class Profile implements GrantedAuthority {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String nome;

    @ManyToMany(mappedBy = "perfis")
    private List<Usuario> usuarios;

    @Override
    public String getAuthority() {
        return this.nome;
    }
}
