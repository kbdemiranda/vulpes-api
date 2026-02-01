package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.AssinanteDTO;
import io.github.vulpes.applications.dto.PlataformaResumoDTO;
import io.github.vulpes.applications.service.AssinanteService;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.AssinantePlataforma;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.domain.enums.CicloCobranca;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssinanteServiceImpl implements AssinanteService {

    private final AssinanteRepository assinanteRepository;
    private final PlataformaRepository plataformaRepository;
    private final AssinantePlataformaRepository assinantePlataformaRepository;
    private final ModelMapper modelMapper;

    public AssinanteServiceImpl(AssinanteRepository assinanteRepository, PlataformaRepository plataformaRepository, AssinantePlataformaRepository assinantePlataformaRepository, ModelMapper modelMapper) {
        this.assinanteRepository = assinanteRepository;
        this.plataformaRepository = plataformaRepository;
        this.assinantePlataformaRepository = assinantePlataformaRepository;
        this.modelMapper = modelMapper;
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

        // Somente considerar plataformas com ciclo MENSAL no cálculo do valorPorMes
        BigDecimal valorPorMes = BigDecimal.ZERO;
        for (Plataforma plataforma : plataformas) {
            CicloCobranca ciclo = plataforma.getCicloCobranca() == null ? CicloCobranca.MENSAL : plataforma.getCicloCobranca();
            if (ciclo != CicloCobranca.MENSAL) {
                continue; // ignora semestral/anual
            }

            BigDecimal valorPlataforma = plataforma.getPreco() == null ? BigDecimal.ZERO : plataforma.getPreco();
            int vagasOcupadas = (plataforma.getTotalVagas() == null ? 0 : plataforma.getTotalVagas()) - (plataforma.getVagasDisponiveis() == null ? 0 : plataforma.getVagasDisponiveis());

            if (vagasOcupadas > 0) {
                valorPorMes = valorPorMes.add(valorPlataforma.divide(new BigDecimal(vagasOcupadas), 2, RoundingMode.HALF_UP));
            }
        }

        AssinanteDTO assinanteDTO = modelMapper.map(assinante, AssinanteDTO.class);

        // Mapear plataformas com cálculo de precoMensal/precoIndividual
        assinanteDTO.setPlataformasAssociadas(plataformasMapper(plataformas));
        assinanteDTO.setValorPorMes(valorPorMes);

        return assinanteDTO;
    }

    private List<PlataformaResumoDTO> plataformasMapper(List<Plataforma> plataformas) {
        List<PlataformaResumoDTO> resumoDTOList = new ArrayList<>(); // Inicializa a lista fora do loop

        for (Plataforma plataforma : plataformas) {
            int totalVagasInt = plataforma.getTotalVagas() == null ? 0 : plataforma.getTotalVagas();
            int vagasDisponiveisInt = plataforma.getVagasDisponiveis() == null ? 0 : plataforma.getVagasDisponiveis();

            BigDecimal totalVagas = new BigDecimal(totalVagasInt);
            BigDecimal vagasDisponiveis = new BigDecimal(vagasDisponiveisInt);
            BigDecimal assinantesAtuais = totalVagas.subtract(vagasDisponiveis);

            PlataformaResumoDTO plataformaResumoDTO = modelMapper.map(plataforma, PlataformaResumoDTO.class);

            BigDecimal preco = plataforma.getPreco() == null ? BigDecimal.ZERO : plataforma.getPreco();
            CicloCobranca ciclo = plataforma.getCicloCobranca() == null ? CicloCobranca.MENSAL : plataforma.getCicloCobranca();

            BigDecimal precoMensal;
            switch (ciclo) {
                case SEMESTRAL:
                    precoMensal = preco.divide(new BigDecimal(6), 2, RoundingMode.HALF_UP);
                    break;
                case ANUAL:
                    precoMensal = preco.divide(new BigDecimal(12), 2, RoundingMode.HALF_UP);
                    break;
                case MENSAL:
                default:
                    precoMensal = preco;
            }

            // Se não houver assinantes atuais (0), fallback para precoMensal integral
            BigDecimal precoIndividual;
            if (assinantesAtuais.compareTo(BigDecimal.ZERO) <= 0) {
                precoIndividual = precoMensal;
            } else {
                precoIndividual = precoMensal.divide(assinantesAtuais, 2, RoundingMode.HALF_UP);
            }

            plataformaResumoDTO.setPrecoMensal(precoMensal);
            plataformaResumoDTO.setPrecoIndividual(precoIndividual);
            resumoDTOList.add(plataformaResumoDTO); // Adiciona o DTO mapeado à lista
        }
        return resumoDTOList; // Retorna a lista completa ao final
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
