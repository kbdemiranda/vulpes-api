package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.SubscriberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriberService {
    Page<SubscriberDTO> listSubscribers(String name, Pageable pageable);
    SubscriberDTO getSubscriber(Long id);
    SubscriberDTO createSubscriber(SubscriberDTO dto);
    SubscriberDTO updateSubscriber(Long id, SubscriberDTO dto);
    void deleteSubscriber(Long id);

    void associatePlatforms(Long subscriberId, List<Long> platformIds);
    void disassociatePlatform(Long subscriberId, Long platformId);

}
