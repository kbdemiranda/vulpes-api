package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.AssinanteDTO;
import io.github.vulpes.applications.dto.PlataformaResumoDTO;
import io.github.vulpes.applications.service.AssinanteService;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.AssinantePlataforma;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.AssinantePlataformaRepository;
import io.github.vulpes.infrastructure.jpa.AssinanteRepository;
import io.github.vulpes.infrastructure.jpa.PlataformaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssinanteServiceImpl implements AssinanteService {

    private final AssinanteRepository assinanteRepository;
    private final PlataformaRepository plataformaRepository;
    private final AssinantePlataformaRepository assinantePlataformaRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public AssinanteServiceImpl(AssinanteRepository assinanteRepository, PlataformaRepository plataformaRepository, AssinantePlataformaRepository assinantePlataformaRepository) {
        this.assinanteRepository = assinanteRepository;
        this.plataformaRepository = plataformaRepository;
        this.assinantePlataformaRepository = assinantePlataformaRepository;
    }

    @Override
    public Page<AssinanteDTO> listarAssinantes(String nome, Pageable pageable) {
        Page<Assinante> assinantes = assinanteRepository.findAssinante(nome, pageable);
        return assinantes.map(assinante -> modelMapper.map(assinante, AssinanteDTO.class));
    }

    @Override
    public AssinanteDTO buscarAssinante(Long id) {
        Assinante assinante = getAssinante(id);

        List<Long> plataformasIds = assinantePlataformaRepository.findPlataformaIdsByAssinanteId(assinante.getId());

        List<Plataforma> plataformas = getPlatataformas(plataformasIds);

        BigDecimal valorPorMes = BigDecimal.ZERO;
        for (Plataforma plataforma : plataformas) {
            BigDecimal valorPlataforma = plataforma.getPreco();
            int vagasOcupadas = plataforma.getTotalVagas() - plataforma.getVagasDisponiveis();

            if (vagasOcupadas > 0) {
                valorPorMes = valorPorMes.add(valorPlataforma.divide(new BigDecimal(vagasOcupadas), 2, RoundingMode.HALF_UP));
            }

        }

        AssinanteDTO assinanteDTO = modelMapper.map(assinante, AssinanteDTO.class);
        assinanteDTO.setPlataformasAssociadas(plataformas.stream()
                .map(plataforma -> modelMapper.map(plataforma, PlataformaResumoDTO.class))
                .collect(Collectors.toList()));
        assinanteDTO.setValorPorMes(valorPorMes);


        return assinanteDTO;
    }

    @Override
    public AssinanteDTO cadastrarAssinante(AssinanteDTO dto) {
        Assinante assinante = Assinante.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .cadastradoEm(LocalDateTime.now())
                .build();

        assinante = assinanteRepository.save(assinante);
        return modelMapper.map(assinante, AssinanteDTO.class);
    }

    @Override
    public AssinanteDTO atualizarAssinante(Long id, AssinanteDTO dto) {
        Assinante assinante = getAssinante(id);

        assinante.setNome(dto.getNome());
        assinante.setEmail(dto.getEmail());
        assinante.setAtualizadoEm(LocalDateTime.now());

        assinante = assinanteRepository.save(assinante);
        return modelMapper.map(assinante, AssinanteDTO.class);
    }

    @Override
    public void excluirAssinante(Long id) {
        Assinante assinante = getAssinante(id);
        assinanteRepository.deleteAssinante(assinante.getId());
    }

    @Override
    public void associarPlataformas(Long assinanteId, List<Long> plataformaIds) {
        Assinante assinante = getAssinante(assinanteId);

        for (Long plataformaId : plataformaIds) {
            Plataforma plataforma = getPlataforma(plataformaId);

            if (plataforma.getVagasDisponiveis() <= 0){
                throw new VulpesException(400, "Plataforma sem vagas disponíveis");
            }

            AssinantePlataforma assinantePlataforma = AssinantePlataforma.builder()
                    .assinante(assinante)
                    .plataforma(plataforma)
                    .cadastradoEm(LocalDateTime.now())
                    .build();

            assinantePlataformaRepository.save(assinantePlataforma);

            plataforma.setVagasDisponiveis(plataforma.getVagasDisponiveis() - 1);
            plataformaRepository.save(plataforma);
        }

    }

    @Override
    public void desassociarPlataforma(Long assinanteId, Long plataformaId) {
        Assinante assinante = getAssinante(assinanteId);
        Plataforma plataforma = getPlataforma(plataformaId);

        assinantePlataformaRepository.deleteAssinantePlataforma(assinante.getId(), plataforma.getId());
        plataforma.setVagasDisponiveis(plataforma.getVagasDisponiveis() + 1);
        plataformaRepository.save(plataforma);
    }

    private Assinante getAssinante(Long id) {
        return assinanteRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Assinante não encontrado"));
    }

    private Plataforma getPlataforma(Long id) {
        return plataformaRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Plataforma não encontrada"));
    }

    private List<Plataforma> getPlatataformas(List<Long> ids) {
        return plataformaRepository.findAllById(ids);
    }
}

