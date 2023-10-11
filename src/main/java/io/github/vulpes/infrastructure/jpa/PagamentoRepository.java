package io.github.vulpes.infrastructure.jpa;

import io.github.vulpes.domain.models.Pagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository  extends JpaRepository<Pagamento, Long> {
    @Query("SELECT p FROM Pagamento p WHERE p.assinante.nome LIKE %:nomeAssinante% and p.excluidoEm is null")
    Page<Pagamento> findPagamentos(@Param("nomeAssinante") String nomeAssinante, Pageable pageable);

    @Query("SELECT p FROM Pagamento p WHERE p.assinante.id = :idAssinante and p.excluidoEm is null")
    Page<Pagamento> findPagamentosAssinante(@Param("idAssinante") Long idAssinante, Pageable pageable);

}
