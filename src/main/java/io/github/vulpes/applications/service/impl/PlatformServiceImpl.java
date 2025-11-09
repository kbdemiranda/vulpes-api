package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.PlatformDTO;
import io.github.vulpes.applications.service.PlatformService;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.PlatformRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public PlatformServiceImpl(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    @Override
    public Page<PlatformDTO> listPlatforms(String name, Pageable pageable) {
        Page<Plataforma> platforms = platformRepository.findPlataforma(name, pageable);
        return platforms.map(platform -> modelMapper.map(platform, PlatformDTO.class));
    }

    @Override
    public PlatformDTO getPlatform(Long id) {
        Plataforma platform = getPlatformEntity(id);
        return modelMapper.map(platform, PlatformDTO.class);
    }

    @Override
    public PlatformDTO createPlatform(PlatformDTO dto) {
        Plataforma platform = Plataforma.builder()
                .nome(dto.getName())
                .preco(dto.getPrice())
                .url(dto.getUrl())
                .tipoServico(dto.getServiceType())
                .totalVagas(dto.getTotalSlots())
                .vagasDisponiveis(dto.getTotalSlots())
                .cadastradoEm(LocalDateTime.now())
                .build();

        platform = platformRepository.save(platform);
        return modelMapper.map(platform, PlatformDTO.class);
    }

    @Override
    public PlatformDTO updatePlatform(Long id, PlatformDTO dto) {
        Plataforma platform = getPlatformEntity(id);

        platform.setNome(dto.getName());
        platform.setPreco(dto.getPrice());
        platform.setUrl(dto.getUrl());
        platform.setTipoServico(dto.getServiceType());
        platform.setTotalVagas(dto.getTotalSlots());
        platform.setAtualizadoEm(LocalDateTime.now());

        platform = platformRepository.save(platform);
        return modelMapper.map(platform, PlatformDTO.class);
    }

    @Override
    public void deletePlatform(Long id) {
        Plataforma platform = getPlatformEntity(id);
        // Hard delete to satisfy integration tests expectations
        platformRepository.delete(platform);
    }

    private Plataforma getPlatformEntity(Long id){
        return platformRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Platform not found"));
    }
}
