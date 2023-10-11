package io.github.vulpes.applications.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.github.vulpes.applications.dto.AssinanteDTO;
import io.github.vulpes.applications.service.impl.AssinanteServiceImpl;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.AssinantePlataforma;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.jpa.AssinantePlataformaRepository;
import io.github.vulpes.infrastructure.jpa.AssinanteRepository;
import io.github.vulpes.infrastructure.jpa.PlataformaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class AssinanteServiceTest {

    @Mock
    private AssinanteRepository assinanteRepository;
    @Mock
    private PlataformaRepository plataformaRepository;
    @Mock
    private AssinantePlataformaRepository assinantePlataformaRepository;

    @Mock
    private final ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private AssinanteServiceImpl assinanteService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        assinanteService = new AssinanteServiceImpl(assinanteRepository, plataformaRepository, assinantePlataformaRepository);
    }


    @Test
    void testBuscarAssinante() {
        Long id = 1L;
        Assinante assinante = new Assinante();
        assinante.setId(id);
        assinante.setNome("João");

        AssinanteDTO expectedDto = new AssinanteDTO();
        expectedDto.setId(id);
        expectedDto.setNome("João");
        expectedDto.setPlataformasAssociadas(new ArrayList<>());
        expectedDto.setValorPorMes(BigDecimal.ZERO);


        when(assinanteRepository.findById(id)).thenReturn(Optional.of(assinante));

        AssinanteDTO resultDto = assinanteService.buscarAssinante(id);

        assertEquals(expectedDto, resultDto);
    }

    @Test
    void testListarAssinantes() {
        // Criar dados de teste
        Assinante assinante1 = new Assinante();
        assinante1.setId(1L);
        assinante1.setNome("João");

        Assinante assinante2 = new Assinante();
        assinante2.setId(2L);
        assinante2.setNome("Maria");

        List<Assinante> assinantes = Arrays.asList(assinante1, assinante2);

        // Criar uma página mockada
        Page<Assinante> page = new PageImpl<>(assinantes);

        // Mockar o repositório para retornar a página mockada
        when(assinanteRepository.findAssinante("", PageRequest.of(0, 10))).thenReturn(page);

        Page<AssinanteDTO> assinanteDTOS = page.map(assinante -> modelMapper.map(assinante, AssinanteDTO.class));

        // Chamar o método a ser testado
        Page<AssinanteDTO> result = assinanteService.listarAssinantes("", PageRequest.of(0, 10));

        // Verificar se o resultado está correto
        assertEquals(assinanteDTOS, result);
    }

    @Test
    void testCadastrarAssinante() {
        // Criar um DTO de entrada
        AssinanteDTO inputDto = new AssinanteDTO();
        inputDto.setNome("João");
        // ... (defina outros campos conforme necessário)

        // Criar um objeto Assinante que você espera ser salvo
        Assinante expectedAssinante = new Assinante();
        expectedAssinante.setNome("João");
        // ... (defina outros campos conforme necessário)

        // Criar um DTO de saída (geralmente, isso seria retornado pelo método de serviço)
        AssinanteDTO outputDto = new AssinanteDTO();
        outputDto.setNome("João");
        // ... (defina outros campos conforme necessário)

        // Mockar o comportamento do modelMapper
        when(modelMapper.map(inputDto, Assinante.class)).thenReturn(expectedAssinante);
        when(modelMapper.map(expectedAssinante, AssinanteDTO.class)).thenReturn(outputDto);

        // Mockar o comportamento do repositório
        when(assinanteRepository.save(expectedAssinante)).thenReturn(expectedAssinante);

        // Chamar o método a ser testado
        AssinanteDTO resultDto = assinanteService.cadastrarAssinante(inputDto);

        // Verificar se o resultado está correto
        assertEquals(outputDto, resultDto);
    }

    @Test
    void testAtualizarAssinante(){
        Long id = 1L;
        AssinanteDTO inputDto = new AssinanteDTO();
        inputDto.setNome("João");

        Assinante expectedAssinante = new Assinante();
        expectedAssinante.setId(id);
        expectedAssinante.setNome("João");

        AssinanteDTO outputDto = new AssinanteDTO();
        outputDto.setId(id);
        outputDto.setNome("João");

        when(assinanteRepository.findById(id)).thenReturn(Optional.of(expectedAssinante));
        when(modelMapper.map(inputDto, Assinante.class)).thenReturn(expectedAssinante);
        when(modelMapper.map(expectedAssinante, AssinanteDTO.class)).thenReturn(outputDto);
        when(assinanteRepository.save(expectedAssinante)).thenReturn(expectedAssinante);

        AssinanteDTO resultDto = assinanteService.atualizarAssinante(id, inputDto);

        assertEquals(outputDto.getId(), resultDto.getId());

    }

    @Test
    void testExcluirAssinante(){
        Long id = 1L;

        // Arrange
        Assinante existingAssinante = new Assinante();
        existingAssinante.setId(id);
        existingAssinante.setNome("João");

        when(assinanteRepository.findById(id)).thenReturn(Optional.of(existingAssinante));


        // Act
        assinanteService.excluirAssinante(id);

        // Assert
        verify(assinanteRepository).deleteAssinante(id);
    }

    @Test
    void testAssociarPlataformas() {
        Long assinanteId = 1L;
        List<Long> plataformaIds = Arrays.asList(1L, 2L);

        Assinante assinante = new Assinante();
        assinante.setId(assinanteId);

        Plataforma plataforma1 = new Plataforma();
        plataforma1.setId(1L);
        plataforma1.setVagasDisponiveis(3);

        Plataforma plataforma2 = new Plataforma();
        plataforma2.setId(2L);
        plataforma2.setVagasDisponiveis(2);

        // Mockar o comportamento dos repositórios
        when(assinanteRepository.findById(assinanteId)).thenReturn(Optional.of(assinante));
        when(plataformaRepository.findById(1L)).thenReturn(Optional.of(plataforma1));
        when(plataformaRepository.findById(2L)).thenReturn(Optional.of(plataforma2));

        // Chamar o método a ser testado
        assinanteService.associarPlataformas(assinanteId, plataformaIds);

        // Verificar se os métodos corretos foram chamados nos repositórios
        verify(plataformaRepository).save(plataforma1);
        verify(plataformaRepository).save(plataforma2);
        verify(assinantePlataformaRepository, times(2)).save(any(AssinantePlataforma.class));

        // Verificar se as vagas disponíveis foram decrementadas
        assertEquals(2, plataforma1.getVagasDisponiveis());
        assertEquals(1, plataforma2.getVagasDisponiveis());
    }

    @Test
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

        // Mockar o comportamento dos repositórios
        when(assinanteRepository.findById(assinanteId)).thenReturn(Optional.of(assinante));
        when(plataformaRepository.findById(plataformaId)).thenReturn(Optional.of(plataforma));

        // Chamar o método a ser testado
        assinanteService.desassociarPlataforma(assinanteId, plataformaId);

        // Verificar se os métodos corretos foram chamados nos repositórios
        verify(assinantePlataformaRepository).deleteAssinantePlataforma(assinanteId, plataformaId);
        verify(plataformaRepository).save(plataforma);

        // Verificar se as vagas disponíveis foram incrementadas
        assertEquals(3, plataforma.getVagasDisponiveis());
    }


}