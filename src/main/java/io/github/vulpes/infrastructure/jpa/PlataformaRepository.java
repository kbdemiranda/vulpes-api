package io.github.vulpes.infrastructure.jpa;

import io.github.vulpes.domain.models.Plataforma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {

    @Query("select p from Plataforma p " +
            "where (:nome is null or p.nome like %:nome%) " +
            "and p.excluidoEm is null " +
            "order by p.id asc")
    Page<Plataforma> findPlataforma(@Param("nome") String nome, Pageable pageable);

    @Query("update Plataforma p set p.excluidoEm = current_timestamp() where p.id = :id")
    @Modifying
    void deletePlataforma(@Param("id") Long id);
}
