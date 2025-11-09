package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.SubscriberDTO;
import io.github.vulpes.applications.service.impl.SubscriberServiceImpl;
import io.github.vulpes.domain.models.Subscriber;
import io.github.vulpes.domain.models.SubscriberPlatform;
import io.github.vulpes.domain.models.Platform;
import io.github.vulpes.infrastructure.jpa.SubscriberPlatformRepository;
import io.github.vulpes.infrastructure.jpa.SubscriberRepository;
import io.github.vulpes.infrastructure.jpa.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberServiceTest {

    @Mock
    private SubscriberRepository subscriberRepository;
    @Mock
    private PlatformRepository platformRepository;
    @Mock
    private SubscriberPlatformRepository subscriberPlatformRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SubscriberServiceImpl subscriberService;

    private Subscriber subscriber;
    private SubscriberDTO subscriberDTO;
    private Platform platform;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setName("João Silva");
        subscriber.setEmail("joao@email.com");

        subscriberDTO = new SubscriberDTO();
        subscriberDTO.setId(1L);
        subscriberDTO.setName("João Silva");
        subscriberDTO.setEmail("joao@email.com");
        subscriberDTO.setAssociatedPlatforms(new ArrayList<>());
        subscriberDTO.setMonthlyValue(BigDecimal.ZERO);

        platform = new Platform();
        platform.setId(1L);
        platform.setName("Netflix");
        platform.setPrice(new BigDecimal("29.90"));
        platform.setAvailableSlots(5);
        platform.setTotalSlots(5);
    }


    @Test
    @DisplayName("Should return SubscriberDTO when subscriber exists")
    void testFindSubscriber_Success() {
        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(subscriberPlatformRepository.findPlatformIdsBySubscriberId(1L)).thenReturn(new ArrayList<>());
        when(platformRepository.findAllById(new ArrayList<>())).thenReturn(new ArrayList<>());
        when(modelMapper.map(subscriber, SubscriberDTO.class)).thenReturn(subscriberDTO);

        SubscriberDTO result = subscriberService.findSubscriber(1L);

        assertNotNull(result);
        assertEquals(subscriber.getId(), result.getId());
        assertEquals(subscriber.getName(), result.getName());
        assertEquals(subscriber.getEmail(), result.getEmail());
        assertTrue(result.getAssociatedPlatforms().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getMonthlyValue());

        verify(subscriberRepository).findById(1L);
        verify(subscriberPlatformRepository).findPlatformIdsBySubscriberId(1L);
    }

    @Test
    @DisplayName("Should throw VulpesException when subscriber not found")
    void testFindSubscriber_NotFound() {
        when(subscriberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> subscriberService.findSubscriber(1L));

        verify(subscriberRepository).findById(1L);
        verifyNoInteractions(subscriberPlatformRepository, platformRepository);
    }

    @Test
    @DisplayName("Should return paginated list of SubscriberDTO")
    void testListSubscribers_Success() {
        Subscriber subscriber2 = new Subscriber();
        subscriber2.setId(2L);
        subscriber2.setName("Maria Santos");
        subscriber2.setEmail("maria@email.com");

        List<Subscriber> subscribers = Arrays.asList(subscriber, subscriber2);
        Page<Subscriber> page = new PageImpl<>(subscribers);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(subscriberRepository.findSubscriber("", pageRequest)).thenReturn(page);
        when(modelMapper.map(subscriber, SubscriberDTO.class)).thenReturn(subscriberDTO);
        when(modelMapper.map(subscriber2, SubscriberDTO.class)).thenReturn(createSubscriberDTO(2L, "Maria Santos"));

        Page<SubscriberDTO> result = subscriberService.listSubscribers("", pageRequest);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getName());
        assertEquals("Maria Santos", result.getContent().get(1).getName());
        assertEquals(2, result.getTotalElements());

        verify(subscriberRepository).findSubscriber("", pageRequest);
    }

    @Test
    @DisplayName("Should return empty page when no subscribers found")
    void testListSubscribers_Empty() {
        Page<Subscriber> emptyPage = new PageImpl<>(new ArrayList<>());
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(subscriberRepository.findSubscriber("nonexistent", pageRequest)).thenReturn(emptyPage);

        Page<SubscriberDTO> result = subscriberService.listSubscribers("nonexistent", pageRequest);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(subscriberRepository).findSubscriber("nonexistent", pageRequest);
    }

    private SubscriberDTO createSubscriberDTO(Long id, String name) {
        SubscriberDTO dto = new SubscriberDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail("maria@email.com");
        dto.setAssociatedPlatforms(new ArrayList<>());
        dto.setMonthlyValue(BigDecimal.ZERO);
        return dto;
    }

    @Test
    @DisplayName("Should successfully create new subscriber")
    void testRegisterSubscriber_Success() {
        SubscriberDTO inputDto = new SubscriberDTO();
        inputDto.setName("João Silva");
        inputDto.setEmail("joao@email.com");

        // Mock save to return the passed entity with an ID set (like a DB would)
        when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> {
            Subscriber saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Mock mapper to convert the saved entity to DTO reflecting fields
        when(modelMapper.map(any(Subscriber.class), eq(SubscriberDTO.class))).thenAnswer(invocation -> {
            Subscriber source = invocation.getArgument(0);
            SubscriberDTO dto = new SubscriberDTO();
            dto.setId(source.getId());
            dto.setName(source.getName());
            dto.setEmail(source.getEmail());
            dto.setAssociatedPlatforms(new ArrayList<>());
            dto.setMonthlyValue(BigDecimal.ZERO);
            return dto;
        });

        SubscriberDTO result = subscriberService.registerSubscriber(inputDto);

        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals(1L, result.getId());

        verify(subscriberRepository).save(any(Subscriber.class));
    }

    @Test
    @DisplayName("Should throw exception when saving subscriber with null data")
    void testRegisterSubscriber_NullData() {
        SubscriberDTO inputDto = new SubscriberDTO();
        inputDto.setName(null);
        inputDto.setEmail("sem.nome@email.com");

        // Simulate repository constraint/validation error when saving invalid entity
        when(subscriberRepository.save(any(Subscriber.class)))
                .thenThrow(new RuntimeException("Dados inválidos: nome obrigatório"));

        assertThrows(RuntimeException.class, () -> subscriberService.registerSubscriber(inputDto));

        // Save is attempted and fails due to invalid data
        verify(subscriberRepository, times(1)).save(argThat(s -> s.getName() == null));
        // Mapping should not be called when save fails
        verify(modelMapper, never()).map(any(Subscriber.class), eq(SubscriberDTO.class));
    }

    @Test
    @DisplayName("Should successfully update existing subscriber")
    void testUpdateSubscriber_Success() {
        SubscriberDTO inputDto = new SubscriberDTO();
        inputDto.setName("João Atualizado");
        inputDto.setEmail("joao.updated@email.com");

        Subscriber updatedSubscriber = new Subscriber();
        updatedSubscriber.setId(1L);
        updatedSubscriber.setName("João Atualizado");
        updatedSubscriber.setEmail("joao.updated@email.com");

        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(subscriberRepository.save(any(Subscriber.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Subscriber.class), eq(SubscriberDTO.class))).thenReturn(inputDto);

        SubscriberDTO result = subscriberService.updateSubscriber(1L, inputDto);

        assertNotNull(result);
        assertEquals("João Atualizado", result.getName());
        assertEquals("joao.updated@email.com", result.getEmail());

        verify(subscriberRepository).findById(1L);
        verify(subscriberRepository).save(any(Subscriber.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent subscriber")
    void testUpdateSubscriber_NotFound() {
        SubscriberDTO inputDto = new SubscriberDTO();
        inputDto.setName("João");

        when(subscriberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> subscriberService.updateSubscriber(1L, inputDto));

        verify(subscriberRepository).findById(1L);
        verify(subscriberRepository, never()).save(any(Subscriber.class));
    }

    @Test
    @DisplayName("Should successfully delete existing subscriber")
    void testDeleteSubscriber_Success() {
        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        doNothing().when(subscriberRepository).deleteSubscriber(1L);

        assertDoesNotThrow(() -> subscriberService.deleteSubscriber(1L));

        verify(subscriberRepository).findById(1L);
        verify(subscriberRepository).deleteSubscriber(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent subscriber")
    void testDeleteSubscriber_NotFound() {
        when(subscriberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> subscriberService.deleteSubscriber(1L));

        verify(subscriberRepository).findById(1L);
        verify(subscriberRepository, never()).deleteSubscriber(1L);
    }

    @Test
    @DisplayName("Should successfully associate platforms with subscriber")
    void testAssociatePlatforms_Success() {
        List<Long> platformIds = Arrays.asList(1L, 2L);

        Platform platform2 = new Platform();
        platform2.setId(2L);
        platform2.setName("Prime Video");
        platform2.setAvailableSlots(2);
        platform2.setTotalSlots(3);

        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        when(platformRepository.findById(2L)).thenReturn(Optional.of(platform2));
        when(platformRepository.save(any(Platform.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(subscriberPlatformRepository.save(any(SubscriberPlatform.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> subscriberService.associatePlatforms(1L, platformIds));

        verify(subscriberRepository).findById(1L);
        verify(platformRepository).findById(1L);
        verify(platformRepository).findById(2L);
        verify(platformRepository, times(2)).save(any(Platform.class));
        verify(subscriberPlatformRepository, times(2)).save(any(SubscriberPlatform.class));

        assertEquals(4, platform.getAvailableSlots());
        assertEquals(1, platform2.getAvailableSlots());
    }

    @Test
    @DisplayName("Should throw exception when subscriber not found for association")
    void testAssociatePlatforms_SubscriberNotFound() {
        List<Long> platformIds = Arrays.asList(1L, 2L);

        when(subscriberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> subscriberService.associatePlatforms(1L, platformIds));

        verify(subscriberRepository).findById(1L);
        verifyNoInteractions(platformRepository, subscriberPlatformRepository);
    }

    @Test
    @DisplayName("Should throw exception when platform has no available slots")
    void testAssociatePlatforms_NoAvailableSlots() {
        List<Long> platformIds = Arrays.asList(1L);
        platform.setAvailableSlots(0);

        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));

        assertThrows(RuntimeException.class, () -> subscriberService.associatePlatforms(1L, platformIds));

        verify(subscriberRepository).findById(1L);
        verify(platformRepository).findById(1L);
        verify(platformRepository, never()).save(any(Platform.class));
        verify(subscriberPlatformRepository, never()).save(any(SubscriberPlatform.class));
    }

    @Test
    @DisplayName("Should successfully disassociate platform from subscriber")
    void testDisassociatePlatform() {
        Long subscriberId = 1L;
        Long platformId = 1L;

        Subscriber subscriber = new Subscriber();
        subscriber.setId(subscriberId);

        Platform platform = new Platform();
        platform.setId(platformId);
        platform.setAvailableSlots(2);

        SubscriberPlatform subscriberPlatform = new SubscriberPlatform();
        subscriberPlatform.setSubscriber(subscriber);
        subscriberPlatform.setPlatform(platform);

        when(subscriberRepository.findById(subscriberId)).thenReturn(Optional.of(subscriber));
        when(platformRepository.findById(platformId)).thenReturn(Optional.of(platform));
        doNothing().when(subscriberPlatformRepository).deleteSubscriberPlatform(subscriberId, platformId);

        subscriberService.disassociatePlatform(subscriberId, platformId);

        verify(subscriberPlatformRepository).deleteSubscriberPlatform(subscriberId, platformId);
        verify(platformRepository).save(platform);

        assertEquals(3, platform.getAvailableSlots());
    }


}
