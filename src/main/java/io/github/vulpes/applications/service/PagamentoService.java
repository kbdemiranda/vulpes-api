package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PagamentoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PagamentoService {
    PagamentoDTO registrarPagamento(PagamentoDTO dto);
    PagamentoDTO atualizarPagamento(Long idPagamento, PagamentoDTO dto);
    PagamentoDTO consultarPagamento(Long id);
    Page<PagamentoDTO> listarPagamentos(String nomeAssinante, Pageable pageable);
    Page<PagamentoDTO> listarPagamentosAssinante(Long idAssinante, Pageable pageable);
}
