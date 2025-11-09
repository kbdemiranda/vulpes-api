package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PlatformDTO;
import io.github.vulpes.applications.service.impl.PlatformServiceImpl;
import io.github.vulpes.domain.enums.ServiceType;
import io.github.vulpes.domain.models.Platform;
import io.github.vulpes.infrastructure.jpa.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlatformServiceTest {

    @Mock
    private PlatformRepository platformRepository;

    @InjectMocks
    private PlatformServiceImpl platformService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        platformService = new PlatformServiceImpl(platformRepository);
    }

    @Test
    void testListPlatforms() {
        Platform p1 = Platform.builder().id(1L).name("Netflix").price(BigDecimal.TEN).build();
        Platform p2 = Platform.builder().id(2L).name("Prime").price(BigDecimal.ONE).build();
        List<Platform> platforms = Arrays.asList(p1, p2);
        Page<Platform> page = new PageImpl<>(platforms);

        when(platformRepository.findPlatform("", PageRequest.of(0, 10))).thenReturn(page);

        Page<PlatformDTO> expected = page.map(platform -> modelMapper.map(platform, PlatformDTO.class));
        Page<PlatformDTO> result = platformService.listPlatforms("", PageRequest.of(0, 10));

        assertEquals(expected, result);
    }

    @Test
    void testFindPlatform() {
        Platform platform = Platform.builder().id(1L).name("Netflix").price(BigDecimal.TEN).build();
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));

        PlatformDTO dto = platformService.findPlatform(1L);
        assertEquals(platform.getId(), dto.getId());
        assertEquals(platform.getName(), dto.getName());
    }

    @Test
    void testRegisterPlatform() {
        PlatformDTO dto = new PlatformDTO();
        dto.setName("Netflix");
        dto.setPrice(BigDecimal.TEN);
        dto.setUrl("http://netflix.com");
        dto.setServiceType(ServiceType.STREAMING_VIDEO);
        dto.setTotalSlots(2);

        when(platformRepository.save(any(Platform.class))).thenAnswer(invocation -> {
            Platform p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PlatformDTO result = platformService.registerPlatform(dto);

        ArgumentCaptor<Platform> captor = ArgumentCaptor.forClass(Platform.class);
        verify(platformRepository).save(captor.capture());
        Platform saved = captor.getValue();

        // Verify the entity that was saved
        assertEquals(dto.getName(), saved.getName());
        assertEquals(dto.getPrice(), saved.getPrice());
        assertEquals(dto.getUrl(), saved.getUrl());
        assertEquals(dto.getServiceType(), saved.getServiceType());
        assertEquals(dto.getTotalSlots(), saved.getTotalSlots());
        assertEquals(dto.getTotalSlots(), saved.getAvailableSlots()); // availableSlots should equal totalSlots initially
        assertNotNull(saved.getRegisteredAt());

        // Verify the returned DTO
        assertNotNull(result);
        assertEquals(1L, result.getId()); // ID should be set by the mock
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getPrice(), result.getPrice());
        assertEquals(dto.getUrl(), result.getUrl());
        assertEquals(dto.getServiceType(), result.getServiceType());
        assertEquals(dto.getTotalSlots(), result.getTotalSlots());
    }

    @Test
    void testUpdatePlatform() {
        Platform existing = Platform.builder().id(1L).name("Old").price(BigDecimal.ONE).build();
        when(platformRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(platformRepository.save(any(Platform.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlatformDTO dto = new PlatformDTO();
        dto.setName("Novo");
        dto.setPrice(BigDecimal.TEN);
        dto.setUrl("http://teste.com");
        dto.setServiceType(ServiceType.STREAMING_VIDEO);
        dto.setTotalSlots(3);

        PlatformDTO result = platformService.updatePlatform(1L, dto);

        ArgumentCaptor<Platform> captor = ArgumentCaptor.forClass(Platform.class);
        verify(platformRepository).save(captor.capture());
        Platform saved = captor.getValue();
        assertEquals(dto.getName(), saved.getName());
        assertEquals(dto.getPrice(), saved.getPrice());

        assertEquals(dto.getName(), result.getName());
    }

    @Test
    void testDeletePlatform() {
        Platform platform = Platform.builder().id(1L).name("Netflix").price(BigDecimal.TEN).build();
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));

        platformService.deletePlatform(1L);

        verify(platformRepository).delete(platform);
    }
}
