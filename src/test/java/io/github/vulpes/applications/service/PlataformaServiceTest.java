package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.PlataformaDTO;
import io.github.vulpes.applications.service.impl.PlataformaServiceImpl;
import io.github.vulpes.domain.enums.TipoServico;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.jpa.PlataformaRepository;
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

class PlataformaServiceTest {

    @Mock
    private PlataformaRepository plataformaRepository;

    @InjectMocks
    private PlataformaServiceImpl plataformaService;

    private final ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        plataformaService = new PlataformaServiceImpl(plataformaRepository);
    }

    @Test
    void testListarPlataformas() {
        Plataforma p1 = Plataforma.builder().id(1L).nome("Netflix").preco(BigDecimal.TEN).build();
        Plataforma p2 = Plataforma.builder().id(2L).nome("Prime").preco(BigDecimal.ONE).build();
        List<Plataforma> plataformas = Arrays.asList(p1, p2);
        Page<Plataforma> page = new PageImpl<>(plataformas);

        when(plataformaRepository.findPlataforma("", PageRequest.of(0, 10))).thenReturn(page);

        Page<PlataformaDTO> expected = page.map(plataforma -> modelMapper.map(plataforma, PlataformaDTO.class));
        Page<PlataformaDTO> result = plataformaService.listarPlataformas("", PageRequest.of(0, 10));

        assertEquals(expected, result);
    }

    @Test
    void testBuscarPlataforma() {
        Plataforma plataforma = Plataforma.builder().id(1L).nome("Netflix").preco(BigDecimal.TEN).build();
        when(plataformaRepository.findById(1L)).thenReturn(Optional.of(plataforma));

        PlataformaDTO dto = plataformaService.buscarPlataforma(1L);
        assertEquals(plataforma.getId(), dto.getId());
        assertEquals(plataforma.getNome(), dto.getNome());
    }

    @Test
    void testCadastrarPlataforma() {
        PlataformaDTO dto = new PlataformaDTO();
        dto.setNome("Netflix");
        dto.setPreco(BigDecimal.TEN);
        dto.setUrl("http://netflix.com");
        dto.setTipoServico(TipoServico.STREAMING_VIDEO);
        dto.setTotalVagas(2);

        when(plataformaRepository.save(any(Plataforma.class))).thenAnswer(invocation -> {
            Plataforma p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PlataformaDTO result = plataformaService.cadastrarPlataforma(dto);

        ArgumentCaptor<Plataforma> captor = ArgumentCaptor.forClass(Plataforma.class);
        verify(plataformaRepository).save(captor.capture());
        Plataforma saved = captor.getValue();

        // Verify the entity that was saved
        assertEquals(dto.getNome(), saved.getNome());
        assertEquals(dto.getPreco(), saved.getPreco());
        assertEquals(dto.getUrl(), saved.getUrl());
        assertEquals(dto.getTipoServico(), saved.getTipoServico());
        assertEquals(dto.getTotalVagas(), saved.getTotalVagas());
        assertEquals(dto.getTotalVagas(), saved.getVagasDisponiveis()); // vagasDisponiveis should equal totalVagas initially
        assertNotNull(saved.getCadastradoEm());

        // Verify the returned DTO
        assertNotNull(result);
        assertEquals(1L, result.getId()); // ID should be set by the mock
        assertEquals(dto.getNome(), result.getNome());
        assertEquals(dto.getPreco(), result.getPreco());
        assertEquals(dto.getUrl(), result.getUrl());
        assertEquals(dto.getTipoServico(), result.getTipoServico());
        assertEquals(dto.getTotalVagas(), result.getTotalVagas());
    }

    @Test
    void testAtualizarPlataforma() {
        Plataforma existente = Plataforma.builder().id(1L).nome("Old").preco(BigDecimal.ONE).build();
        when(plataformaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(plataformaRepository.save(any(Plataforma.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlataformaDTO dto = new PlataformaDTO();
        dto.setNome("Novo");
        dto.setPreco(BigDecimal.TEN);
        dto.setUrl("http://teste.com");
        dto.setTipoServico(TipoServico.STREAMING_VIDEO);
        dto.setTotalVagas(3);

        PlataformaDTO result = plataformaService.atualizarPlataforma(1L, dto);

        ArgumentCaptor<Plataforma> captor = ArgumentCaptor.forClass(Plataforma.class);
        verify(plataformaRepository).save(captor.capture());
        Plataforma saved = captor.getValue();
        assertEquals(dto.getNome(), saved.getNome());
        assertEquals(dto.getPreco(), saved.getPreco());

        assertEquals(dto.getNome(), result.getNome());
    }

    @Test
    void testExcluirPlataforma() {
        Plataforma plataforma = Plataforma.builder().id(1L).nome("Netflix").preco(BigDecimal.TEN).build();
        when(plataformaRepository.findById(1L)).thenReturn(Optional.of(plataforma));

        plataformaService.excluirPlataforma(1L);

        verify(plataformaRepository).delete(plataforma);
    }
}
