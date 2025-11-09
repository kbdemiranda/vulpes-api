package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PlatformDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformService {

    Page<PlatformDTO> listPlatforms(String name, Pageable pageable);
    PlatformDTO getPlatform(Long id);
    PlatformDTO createPlatform(PlatformDTO dto);
    PlatformDTO updatePlatform(Long id, PlatformDTO dto);
    void deletePlatform(Long id);

}
