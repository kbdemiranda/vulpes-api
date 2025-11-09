package io.github.vulpes.infrastructure.jpa;

import io.github.vulpes.domain.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByEmail(String email);

    @Query("update Usuario u set u.excluidoEm = CURRENT_TIMESTAMP where u.id = :id")
    @Modifying(clearAutomatically = true)
    @Transactional
    int deleteUsuario(@Param("id") Long id);
}
