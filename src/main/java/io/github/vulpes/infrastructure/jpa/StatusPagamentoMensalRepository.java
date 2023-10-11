package io.github.vulpes.infrastructure.jpa;

import io.github.vulpes.domain.models.StatusPagamentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusPagamentoMensalRepository extends JpaRepository<StatusPagamentoMensal, Long> {
    @Query("SELECT spm.mes FROM StatusPagamentoMensal spm WHERE spm.assinante.id = :idAssinante and spm.pagamento.id = :idPagamento")
    List<Integer> findMesesByAssinanteId(@Param("idAssinante") Long idAssinante, @Param("idPagamento") Long idPagamento);
}
