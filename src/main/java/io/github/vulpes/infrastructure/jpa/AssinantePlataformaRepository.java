package io.github.vulpes.infrastructure.jpa;

import io.github.vulpes.domain.models.AssinantePlataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssinantePlataformaRepository extends JpaRepository<AssinantePlataforma, Long> {
    @Query("SELECT ap.plataforma.id FROM AssinantePlataforma ap WHERE ap.assinante.id = :assinanteId AND ap.excluidoEm IS NULL")
    List<Long> findPlataformaIdsByAssinanteId(@Param("assinanteId") Long assinanteId);

    @Query("update AssinantePlataforma ap set ap.excluidoEm = current_timestamp where ap.assinante.id = :assinanteId and ap.plataforma.id = :plataformaId")
    @Modifying
    void deleteAssinantePlataforma(@Param("assinanteId") Long assinanteId, @Param("plataformaId") Long plataformaId);
}
