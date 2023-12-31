package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.AssinanteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssinanteService {
    Page<AssinanteDTO> listarAssinantes(String nome, Pageable pageable);
    AssinanteDTO buscarAssinante(Long id);
    AssinanteDTO cadastrarAssinante(AssinanteDTO dto);
    AssinanteDTO atualizarAssinante(Long id, AssinanteDTO dto);
    void excluirAssinante(Long id);

    void associarPlataformas(Long assinanteId, List<Long> plataformaIds);
    void desassociarPlataforma(Long assinanteId, Long plataformaId);

}
