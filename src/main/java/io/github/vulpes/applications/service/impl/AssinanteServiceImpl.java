package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.AssinanteDTO;
import io.github.vulpes.applications.service.AssinanteService;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.AssinanteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AssinanteServiceImpl implements AssinanteService {

    private final AssinanteRepository assinanteRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public AssinanteServiceImpl(AssinanteRepository assinanteRepository) {
        this.assinanteRepository = assinanteRepository;
    }

    @Override
    public Page<AssinanteDTO> listarAssinantes(String nome, Pageable pageable) {
        Page<Assinante> assinantes = assinanteRepository.findAssinante(nome, pageable);
        return assinantes.map(assinante -> modelMapper.map(assinante, AssinanteDTO.class));
    }

    @Override
    public AssinanteDTO buscarAssinante(Long id) {
        Assinante assinante = getAssinante(id);
        return modelMapper.map(assinante, AssinanteDTO.class);
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

    private Assinante getAssinante(Long id) {
        return assinanteRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Assinante não encontrado"));
    }
}

