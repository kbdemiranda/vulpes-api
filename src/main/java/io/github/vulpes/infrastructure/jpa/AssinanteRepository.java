package io.github.vulpes.infrastructure.jpa;

import io.github.vulpes.domain.models.Assinante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssinanteRepository extends JpaRepository<Assinante, Long> {

    @Query("select a from Assinante a " +
            "where (:nome is null or a.nome like %:nome%) " +
            "and a.excluidoEm is null")
    Page<Assinante> findAssinante(@Param("nome") String nome, Pageable pageable);

    @Query("update Assinante a set a.excluidoEm = current_timestamp() where a.id = :id")
    @Modifying
    void deleteAssinante(@Param("id") Long id);
}

