package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.AssinanteDTO;
import io.github.vulpes.applications.service.impl.AssinanteServiceImpl;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.AssinantePlataforma;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.jpa.AssinantePlataformaRepository;
import io.github.vulpes.infrastructure.jpa.AssinanteRepository;
import io.github.vulpes.infrastructure.jpa.PlataformaRepository;
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
class AssinanteServiceTest {

    @Mock
    private AssinanteRepository assinanteRepository;
    @Mock
    private PlataformaRepository plataformaRepository;
    @Mock
    private AssinantePlataformaRepository assinantePlataformaRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AssinanteServiceImpl assinanteService;

    private Assinante assinante;
    private AssinanteDTO assinanteDTO;
    private Plataforma plataforma;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        assinante = new Assinante();
        assinante.setId(1L);
        assinante.setNome("João Silva");
        assinante.setEmail("joao@email.com");

        assinanteDTO = new AssinanteDTO();
        assinanteDTO.setId(1L);
        assinanteDTO.setNome("João Silva");
        assinanteDTO.setEmail("joao@email.com");
        assinanteDTO.setPlataformasAssociadas(new ArrayList<>());
        assinanteDTO.setValorPorMes(BigDecimal.ZERO);

        plataforma = new Plataforma();
        plataforma.setId(1L);
        plataforma.setNome("Netflix");
        plataforma.setPreco(new BigDecimal("29.90"));
        plataforma.setVagasDisponiveis(5);
        plataforma.setTotalVagas(5);
    }


    @Test
    @DisplayName("Should return AssinanteDTO when assinante exists")
    void testBuscarAssinante_Success() {
        when(assinanteRepository.findById(1L)).thenReturn(Optional.of(assinante));
        when(assinantePlataformaRepository.findPlataformaIdsByAssinanteId(1L)).thenReturn(new ArrayList<>());
        when(plataformaRepository.findAllById(new ArrayList<>())).thenReturn(new ArrayList<>());
        when(modelMapper.map(assinante, AssinanteDTO.class)).thenReturn(assinanteDTO);

        AssinanteDTO result = assinanteService.buscarAssinante(1L);

        assertNotNull(result);
        assertEquals(assinante.getId(), result.getId());
        assertEquals(assinante.getNome(), result.getNome());
        assertEquals(assinante.getEmail(), result.getEmail());
        assertTrue(result.getPlataformasAssociadas().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getValorPorMes());
        
        verify(assinanteRepository).findById(1L);
        verify(assinantePlataformaRepository).findPlataformaIdsByAssinanteId(1L);
    }

    @Test
    @DisplayName("Should throw VulpesException when assinante not found")
    void testBuscarAssinante_NotFound() {
        when(assinanteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> assinanteService.buscarAssinante(1L));
        
        verify(assinanteRepository).findById(1L);
        verifyNoInteractions(assinantePlataformaRepository, plataformaRepository);
    }

    @Test
    @DisplayName("Should return paginated list of AssinanteDTO")
    void testListarAssinantes_Success() {
        Assinante assinante2 = new Assinante();
        assinante2.setId(2L);
        assinante2.setNome("Maria Santos");
        assinante2.setEmail("maria@email.com");

        List<Assinante> assinantes = Arrays.asList(assinante, assinante2);
        Page<Assinante> page = new PageImpl<>(assinantes);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(assinanteRepository.findAssinante("", pageRequest)).thenReturn(page);
        when(modelMapper.map(assinante, AssinanteDTO.class)).thenReturn(assinanteDTO);
        when(modelMapper.map(assinante2, AssinanteDTO.class)).thenReturn(createAssinanteDTO(2L, "Maria Santos"));

        Page<AssinanteDTO> result = assinanteService.listarAssinantes("", pageRequest);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getNome());
        assertEquals("Maria Santos", result.getContent().get(1).getNome());
        assertEquals(2, result.getTotalElements());
        
        verify(assinanteRepository).findAssinante("", pageRequest);
    }

    @Test
    @DisplayName("Should return empty page when no assinantes found")
    void testListarAssinantes_Empty() {
        Page<Assinante> emptyPage = new PageImpl<>(new ArrayList<>());
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(assinanteRepository.findAssinante("nonexistent", pageRequest)).thenReturn(emptyPage);

        Page<AssinanteDTO> result = assinanteService.listarAssinantes("nonexistent", pageRequest);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        
        verify(assinanteRepository).findAssinante("nonexistent", pageRequest);
    }

    private AssinanteDTO createAssinanteDTO(Long id, String nome) {
        AssinanteDTO dto = new AssinanteDTO();
        dto.setId(id);
        dto.setNome(nome);
        dto.setEmail("maria@email.com");
        dto.setPlataformasAssociadas(new ArrayList<>());
        dto.setValorPorMes(BigDecimal.ZERO);
        return dto;
    }

    @Test
    @DisplayName("Should successfully create new assinante")
    void testCadastrarAssinante_Success() {
        AssinanteDTO inputDto = new AssinanteDTO();
        inputDto.setNome("João Silva");
        inputDto.setEmail("joao@email.com");

        // Mock save to return the passed entity with an ID set (like a DB would)
        when(assinanteRepository.save(any(Assinante.class))).thenAnswer(invocation -> {
            Assinante saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Mock mapper to convert the saved entity to DTO reflecting fields
        when(modelMapper.map(any(Assinante.class), eq(AssinanteDTO.class))).thenAnswer(invocation -> {
            Assinante source = invocation.getArgument(0);
            AssinanteDTO dto = new AssinanteDTO();
            dto.setId(source.getId());
            dto.setNome(source.getNome());
            dto.setEmail(source.getEmail());
            dto.setPlataformasAssociadas(new ArrayList<>());
            dto.setValorPorMes(BigDecimal.ZERO);
            return dto;
        });

        AssinanteDTO result = assinanteService.cadastrarAssinante(inputDto);

        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals(1L, result.getId());

        verify(assinanteRepository).save(any(Assinante.class));
    }

    @Test
    @DisplayName("Should throw exception when saving assinante with null data")
    void testCadastrarAssinante_NullData() {
        AssinanteDTO inputDto = new AssinanteDTO();
        inputDto.setNome(null);
        inputDto.setEmail("sem.nome@email.com");

        // Simulate repository constraint/validation error when saving invalid entity
        when(assinanteRepository.save(any(Assinante.class)))
                .thenThrow(new RuntimeException("Dados inválidos: nome obrigatório"));

        assertThrows(RuntimeException.class, () -> assinanteService.cadastrarAssinante(inputDto));

        // Save is attempted and fails due to invalid data
        verify(assinanteRepository, times(1)).save(argThat(a -> a.getNome() == null));
        // Mapping should not be called when save fails
        verify(modelMapper, never()).map(any(Assinante.class), eq(AssinanteDTO.class));
    }

    @Test
    @DisplayName("Should successfully update existing assinante")
    void testAtualizarAssinante_Success() {
        AssinanteDTO inputDto = new AssinanteDTO();
        inputDto.setNome("João Atualizado");
        inputDto.setEmail("joao.updated@email.com");

        Assinante updatedAssinante = new Assinante();
        updatedAssinante.setId(1L);
        updatedAssinante.setNome("João Atualizado");
        updatedAssinante.setEmail("joao.updated@email.com");

        when(assinanteRepository.findById(1L)).thenReturn(Optional.of(assinante));
        when(assinanteRepository.save(any(Assinante.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Assinante.class), eq(AssinanteDTO.class))).thenReturn(inputDto);

        AssinanteDTO result = assinanteService.atualizarAssinante(1L, inputDto);

        assertNotNull(result);
        assertEquals("João Atualizado", result.getNome());
        assertEquals("joao.updated@email.com", result.getEmail());
        
        verify(assinanteRepository).findById(1L);
        verify(assinanteRepository).save(any(Assinante.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent assinante")
    void testAtualizarAssinante_NotFound() {
        AssinanteDTO inputDto = new AssinanteDTO();
        inputDto.setNome("João");

        when(assinanteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> assinanteService.atualizarAssinante(1L, inputDto));
        
        verify(assinanteRepository).findById(1L);
        verify(assinanteRepository, never()).save(any(Assinante.class));
    }

    @Test
    @DisplayName("Should successfully delete existing assinante")
    void testExcluirAssinante_Success() {
        when(assinanteRepository.findById(1L)).thenReturn(Optional.of(assinante));
        doNothing().when(assinanteRepository).deleteAssinante(1L);

        assertDoesNotThrow(() -> assinanteService.excluirAssinante(1L));
        
        verify(assinanteRepository).findById(1L);
        verify(assinanteRepository).deleteAssinante(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent assinante")
    void testExcluirAssinante_NotFound() {
        when(assinanteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> assinanteService.excluirAssinante(1L));
        
        verify(assinanteRepository).findById(1L);
        verify(assinanteRepository, never()).deleteAssinante(1L);
    }

    @Test
    @DisplayName("Should successfully associate platforms with assinante")
    void testAssociarPlataformas_Success() {
        List<Long> plataformaIds = Arrays.asList(1L, 2L);

        Plataforma plataforma2 = new Plataforma();
        plataforma2.setId(2L);
        plataforma2.setNome("Prime Video");
        plataforma2.setVagasDisponiveis(2);
        plataforma2.setTotalVagas(3);

        when(assinanteRepository.findById(1L)).thenReturn(Optional.of(assinante));
        when(plataformaRepository.findById(1L)).thenReturn(Optional.of(plataforma));
        when(plataformaRepository.findById(2L)).thenReturn(Optional.of(plataforma2));
        when(plataformaRepository.save(any(Plataforma.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(assinantePlataformaRepository.save(any(AssinantePlataforma.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> assinanteService.associarPlataformas(1L, plataformaIds));

        verify(assinanteRepository).findById(1L);
        verify(plataformaRepository).findById(1L);
        verify(plataformaRepository).findById(2L);
        verify(plataformaRepository, times(2)).save(any(Plataforma.class));
        verify(assinantePlataformaRepository, times(2)).save(any(AssinantePlataforma.class));

        assertEquals(4, plataforma.getVagasDisponiveis());
        assertEquals(1, plataforma2.getVagasDisponiveis());
    }

    @Test
    @DisplayName("Should throw exception when assinante not found for association")
    void testAssociarPlataformas_AssinanteNotFound() {
        List<Long> plataformaIds = Arrays.asList(1L, 2L);

        when(assinanteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> assinanteService.associarPlataformas(1L, plataformaIds));
        
        verify(assinanteRepository).findById(1L);
        verifyNoInteractions(plataformaRepository, assinantePlataformaRepository);
    }

    @Test
    @DisplayName("Should throw exception when platform has no available slots")
    void testAssociarPlataformas_NoAvailableSlots() {
        List<Long> plataformaIds = Arrays.asList(1L);
        plataforma.setVagasDisponiveis(0);

        when(assinanteRepository.findById(1L)).thenReturn(Optional.of(assinante));
        when(plataformaRepository.findById(1L)).thenReturn(Optional.of(plataforma));

        assertThrows(RuntimeException.class, () -> assinanteService.associarPlataformas(1L, plataformaIds));
        
        verify(assinanteRepository).findById(1L);
        verify(plataformaRepository).findById(1L);
        verify(plataformaRepository, never()).save(any(Plataforma.class));
        verify(assinantePlataformaRepository, never()).save(any(AssinantePlataforma.class));
    }

    @Test
    @DisplayName("Should successfully desassociate platform from assinante")
    void testDesassociarPlataforma() {
        Long assinanteId = 1L;
        Long plataformaId = 1L;

        Assinante assinante = new Assinante();
        assinante.setId(assinanteId);

        Plataforma plataforma = new Plataforma();
        plataforma.setId(plataformaId);
        plataforma.setVagasDisponiveis(2);

        AssinantePlataforma assinantePlataforma = new AssinantePlataforma();
        assinantePlataforma.setAssinante(assinante);
        assinantePlataforma.setPlataforma(plataforma);

        when(assinanteRepository.findById(assinanteId)).thenReturn(Optional.of(assinante));
        when(plataformaRepository.findById(plataformaId)).thenReturn(Optional.of(plataforma));
        doNothing().when(assinantePlataformaRepository).deleteAssinantePlataforma(assinanteId, plataformaId);

        assinanteService.desassociarPlataforma(assinanteId, plataformaId);

        verify(assinantePlataformaRepository).deleteAssinantePlataforma(assinanteId, plataformaId);
        verify(plataformaRepository).save(plataforma);

        assertEquals(3, plataforma.getVagasDisponiveis());
    }


}
