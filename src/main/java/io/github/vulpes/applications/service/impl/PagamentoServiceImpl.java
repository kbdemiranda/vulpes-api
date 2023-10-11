package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.PagamentoDTO;
import io.github.vulpes.applications.service.PagamentoService;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.Pagamento;
import io.github.vulpes.domain.models.StatusPagamentoMensal;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.AssinanteRepository;
import io.github.vulpes.infrastructure.jpa.PagamentoRepository;
import io.github.vulpes.infrastructure.jpa.StatusPagamentoMensalRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.format.TextStyle.FULL;
import static java.util.Locale.getDefault;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final StatusPagamentoMensalRepository statusPagamentoMensalRepository;
    private final AssinanteRepository assinanteRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    public PagamentoServiceImpl(PagamentoRepository pagamentoRepository, StatusPagamentoMensalRepository statusPagamentoMensalRepository, AssinanteRepository assinanteRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.statusPagamentoMensalRepository = statusPagamentoMensalRepository;
        this.assinanteRepository = assinanteRepository;
    }

    @Override
    public PagamentoDTO registrarPagamento(PagamentoDTO dto) {
        Assinante assinante = getAssinante(dto.getAssinanteId());

        Pagamento pagamento = Pagamento.builder()
                .assinante(assinante)
                .valorPago(dto.getValorPago())
                .dataPagamento(dto.getDataPagamento())
                .mesesCobertos(dto.getMesesCobertos())
                .cadastradoEm(LocalDateTime.now())
                .build();

        Pagamento p = pagamentoRepository.save(pagamento);

        atualizarStatusPagamentoMensal(dto, p, assinante);


        return modelMapper.map(p, PagamentoDTO.class);
    }

    @Override
    public PagamentoDTO atualizarPagamento(Long idPagamento, PagamentoDTO dto) {
        Pagamento pagamento = getPagamento(idPagamento);
        pagamento.setValorPago(dto.getValorPago());
        pagamento.setDataPagamento(dto.getDataPagamento());
        pagamento.setMesesCobertos(dto.getMesesCobertos());
        pagamento.setAtualizadoEm(LocalDateTime.now());

        Pagamento p = pagamentoRepository.save(pagamento);

        atualizarStatusPagamentoMensal(dto, p, pagamento.getAssinante());

        return modelMapper.map(p, PagamentoDTO.class);
    }

    @Override
    public PagamentoDTO consultarPagamento(Long id) {
        Pagamento pagamento = getPagamento(id);
        return modelMapper.map(pagamento, PagamentoDTO.class);
    }

    @Override
    public Page<PagamentoDTO> listarPagamentos(String nomeAssinante, Pageable pageable) {
        Page<Pagamento> pagamentos = pagamentoRepository.findPagamentos(nomeAssinante, pageable);
        return pagamentos.map(pagamento -> modelMapper.map(pagamento, PagamentoDTO.class));
    }

    @Override
    public Page<PagamentoDTO> listarPagamentosAssinante(Long idAssinante, Pageable pageable) {
        Page<Pagamento> pagamentos = pagamentoRepository.findPagamentosAssinante(idAssinante, pageable);
        return pagamentos.map(pagamento -> {
            PagamentoDTO dto = modelMapper.map(pagamento, PagamentoDTO.class);

            // Buscando os meses pagos
            List<Integer> mesesPagos = statusPagamentoMensalRepository.findMesesByAssinanteId(idAssinante, pagamento.getId());

            // Convertendo para nomes de meses
            List<String> nomesMeses = mesesPagos.stream()
                    .map(mes -> Month.of(mes).getDisplayName(FULL, getDefault()).toUpperCase())
                    .collect(Collectors.toList());

            dto.setMeses(nomesMeses);

            return dto;
        });
    }

    private void atualizarStatusPagamentoMensal(PagamentoDTO dto, Pagamento pagamento, Assinante assinante) {
        IntStream.range(1, dto.getMesesCobertos() + 1).forEach(i -> {
            StatusPagamentoMensal statusPagamentoMensal = new StatusPagamentoMensal();
            LocalDateTime dataCoberta = dto.getDataPagamento().plusMonths(i - 1);

            statusPagamentoMensal.setAssinante(assinante);
            statusPagamentoMensal.setPagamento(pagamento);
            statusPagamentoMensal.setMes(dataCoberta.getMonthValue());
            statusPagamentoMensal.setAno(dataCoberta.getYear());
            statusPagamentoMensal.setStatusPagamento("PAGO");
            statusPagamentoMensal.setCadastradoEm(LocalDateTime.now());

            statusPagamentoMensalRepository.save(statusPagamentoMensal);
        });


    }

    private Pagamento getPagamento(Long id) {
        return pagamentoRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Pagamento não encontrado"));
    }

    private Assinante getAssinante(Long id) {
        return assinanteRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Assinante não encontrado"));
    }
}
