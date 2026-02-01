package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.PlataformaDTO;
import io.github.vulpes.applications.dto.PlataformaUpdateDTO;
import io.github.vulpes.applications.service.PlataformaService;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.PlataformaRepository;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlataformaServiceImpl implements PlataformaService {

    private final PlataformaRepository plataformaRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public PlataformaServiceImpl(PlataformaRepository plataformaRepository) {
        this.plataformaRepository = plataformaRepository;
    }

    @Override
    public Page<PlataformaDTO> listarPlataformas(String nome, Pageable pageable) {
        Page<Plataforma> plataformas = plataformaRepository.findPlataforma(nome, pageable);
        return plataformas.map(plataforma -> modelMapper.map(plataforma, PlataformaDTO.class));
    }

    @Override
    public PlataformaDTO buscarPlataforma(Long id) {
        Plataforma plataforma = getPlataforma(id);
        return modelMapper.map(plataforma, PlataformaDTO.class);
    }

    @Override
    public PlataformaDTO cadastrarPlataforma(PlataformaDTO dto) {
        Plataforma plataforma = Plataforma.builder()
                .nome(dto.getNome())
                .preco(dto.getPreco())
                .url(dto.getUrl())
                .tipoServico(dto.getTipoServico())
                .totalVagas(dto.getTotalVagas())
                .vagasDisponiveis(dto.getTotalVagas())
                .cadastradoEm(LocalDateTime.now())
                .build();

        plataforma = plataformaRepository.save(plataforma);
        return modelMapper.map(plataforma, PlataformaDTO.class);
    }

    @Override
    public PlataformaDTO atualizarPlataforma(Long id, PlataformaUpdateDTO dto) {
        Plataforma plataforma = getPlataforma(id);

        plataforma.setNome(dto.getNome());
        plataforma.setPreco(dto.getPreco());
        plataforma.setUrl(dto.getUrl());
        plataforma.setTipoServico(dto.getTipoServico());
        atualizaVagas(plataforma, dto.getTotalVagas());
        plataforma.setAtualizadoEm(LocalDateTime.now());

        plataforma = plataformaRepository.save(plataforma);
        return modelMapper.map(plataforma, PlataformaDTO.class);
    }

    private void atualizaVagas(
            Plataforma plataforma,
            @NotNull(message = "O total de vagas não pode ser nulo")
            @Min(value = 1, message = "O total de vagas deve ser pelo menos 1")
            Integer novoTotalVagas
    ) {
        int totalAtual = plataforma.getTotalVagas();
        int vagasDisponiveisAtual = plataforma.getVagasDisponiveis();
        int vagasEmUso = totalAtual - vagasDisponiveisAtual;

        if (novoTotalVagas < vagasEmUso) {
            throw new VulpesException(
                    400,
                    "O total de vagas não pode ser menor que o número de vagas em uso"
            );
        }

        int diferenca = novoTotalVagas - totalAtual;

        if (diferenca > 0) {
            plataforma.setVagasDisponiveis(vagasDisponiveisAtual + diferenca);
        }

        plataforma.setTotalVagas(novoTotalVagas);
    }


    @Override
    public void excluirPlataforma(Long id) {
        Plataforma plataforma = getPlataforma(id);
        // Hard delete to satisfy integration tests expectations
        plataformaRepository.delete(plataforma);
    }

    private Plataforma getPlataforma(Long id){
        return plataformaRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Plataforma não encontrada"));
    }
}
