package io.github.vulpes.applications.service.impl;

import io.github.vulpes.applications.dto.SubscriberDTO;
import io.github.vulpes.applications.dto.PlatformSummaryDTO;
import io.github.vulpes.applications.service.SubscriberService;
import io.github.vulpes.domain.models.Assinante;
import io.github.vulpes.domain.models.SubscriberPlatform;
import io.github.vulpes.domain.models.Plataforma;
import io.github.vulpes.infrastructure.exceptions.VulpesException;
import io.github.vulpes.infrastructure.jpa.SubscriberPlatformRepository;
import io.github.vulpes.infrastructure.jpa.SubscriberRepository;
import io.github.vulpes.infrastructure.jpa.PlatformRepository;
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
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final PlatformRepository platformRepository;
    private final SubscriberPlatformRepository subscriberPlatformRepository;
    private final ModelMapper modelMapper;

    public SubscriberServiceImpl(SubscriberRepository subscriberRepository, PlatformRepository platformRepository, SubscriberPlatformRepository subscriberPlatformRepository, ModelMapper modelMapper) {
        this.subscriberRepository = subscriberRepository;
        this.platformRepository = platformRepository;
        this.subscriberPlatformRepository = subscriberPlatformRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<SubscriberDTO> listSubscribers(String name, Pageable pageable) {
        Page<Assinante> subscribers = subscriberRepository.findAssinante(name, pageable);
        return subscribers.map(subscriber -> modelMapper.map(subscriber, SubscriberDTO.class));
    }

    @Override
    public SubscriberDTO getSubscriber(Long id) {
        Assinante subscriber = getSubscriberEntity(id);

        List<Long> platformIds = subscriberPlatformRepository.findPlataformaIdsByAssinanteId(subscriber.getId());

        List<Plataforma> platforms = getPlatforms(platformIds);

        BigDecimal amountPerMonth = BigDecimal.ZERO;
        for (Plataforma platform : platforms) {
            BigDecimal platformAmount = platform.getPreco();
            int occupiedSlots = platform.getTotalVagas() - platform.getVagasDisponiveis();

            if (occupiedSlots > 0) {
                amountPerMonth = amountPerMonth.add(platformAmount.divide(new BigDecimal(occupiedSlots), 2, RoundingMode.HALF_UP));
            }

        }

        SubscriberDTO subscriberDTO = modelMapper.map(subscriber, SubscriberDTO.class);
        subscriberDTO.setAssociatedPlatforms(platforms.stream()
                .map(platform -> modelMapper.map(platform, PlatformSummaryDTO.class))
                .collect(Collectors.toList()));

        subscriberDTO.setAssociatedPlatforms(platformsMapper(platforms));
        subscriberDTO.setAmountPerMonth(amountPerMonth);


        return subscriberDTO;
    }

    private List<PlatformSummaryDTO> platformsMapper(List<Plataforma> platforms) {
        List<PlatformSummaryDTO> summaryDTOList = new ArrayList<>();


        for (Plataforma platform : platforms) {
            BigDecimal totalSlots = new BigDecimal(platform.getTotalVagas());
            BigDecimal availableSlots = new BigDecimal(platform.getVagasDisponiveis());
            BigDecimal currentSubscribers = totalSlots.subtract(availableSlots);

            PlatformSummaryDTO platformSummaryDTO = modelMapper.map(platform, PlatformSummaryDTO.class);
            platformSummaryDTO.setMonthlyPrice(platform.getPreco());
            platformSummaryDTO.setIndividualPrice(platform.getPreco().divide(currentSubscribers, 2, RoundingMode.HALF_UP));
            summaryDTOList.add(platformSummaryDTO);
        }
        return summaryDTOList;
    }


    @Override
    public SubscriberDTO createSubscriber(SubscriberDTO dto) {
        Assinante subscriber = Assinante.builder()
                .nome(dto.getName())
                .email(dto.getEmail())
                .cadastradoEm(LocalDateTime.now())
                .build();

        subscriber = subscriberRepository.save(subscriber);
        return modelMapper.map(subscriber, SubscriberDTO.class);
    }

    @Override
    public SubscriberDTO updateSubscriber(Long id, SubscriberDTO dto) {
        Assinante subscriber = getSubscriberEntity(id);

        subscriber.setNome(dto.getName());
        subscriber.setEmail(dto.getEmail());
        subscriber.setAtualizadoEm(LocalDateTime.now());

        subscriber = subscriberRepository.save(subscriber);
        return modelMapper.map(subscriber, SubscriberDTO.class);
    }

    @Override
    public void deleteSubscriber(Long id) {
        Assinante subscriber = getSubscriberEntity(id);
        subscriberRepository.deleteAssinante(subscriber.getId());
    }

    @Override
    public void associatePlatforms(Long subscriberId, List<Long> platformIds) {
        Assinante subscriber = getSubscriberEntity(subscriberId);

        for (Long platformId : platformIds) {
            Plataforma platform = getPlatform(platformId);

            if (platform.getVagasDisponiveis() <= 0){
                throw new VulpesException(400, "Platform has no available slots");
            }

            SubscriberPlatform subscriberPlatform = SubscriberPlatform.builder()
                    .assinante(subscriber)
                    .plataforma(platform)
                    .cadastradoEm(LocalDateTime.now())
                    .build();

            subscriberPlatformRepository.save(subscriberPlatform);

            platform.setVagasDisponiveis(platform.getVagasDisponiveis() - 1);
            platformRepository.save(platform);
        }

    }

    @Override
    public void disassociatePlatform(Long subscriberId, Long platformId) {
        Assinante subscriber = getSubscriberEntity(subscriberId);
        Plataforma platform = getPlatform(platformId);

        subscriberPlatformRepository.deleteAssinantePlataforma(subscriber.getId(), platform.getId());
        platform.setVagasDisponiveis(platform.getVagasDisponiveis() + 1);
        platformRepository.save(platform);
    }

    private Assinante getSubscriberEntity(Long id) {
        return subscriberRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Subscriber not found"));
    }

    private Plataforma getPlatform(Long id) {
        return platformRepository.findById(id).orElseThrow(() -> new VulpesException(404, "Platform not found"));
    }

    private List<Plataforma> getPlatforms(List<Long> ids) {
        return platformRepository.findAllById(ids);
    }
}
