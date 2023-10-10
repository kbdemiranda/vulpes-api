package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PlataformaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlataformaService {

    Page<PlataformaDTO> listarPlataformas(String nome, Pageable pageable);
    PlataformaDTO buscarPlataforma(Long id);
    PlataformaDTO cadastrarPlataforma(PlataformaDTO dto);
    PlataformaDTO atualizarPlataforma(Long id, PlataformaDTO dto);
    void excluirPlataforma(Long id);

}
