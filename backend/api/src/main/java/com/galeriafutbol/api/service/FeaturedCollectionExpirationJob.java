package com.galeriafutbol.api.service;

import java.time.OffsetDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.galeriafutbol.api.repository.FeaturedCollectionRepository;

@Component
public class FeaturedCollectionExpirationJob {

    private final FeaturedCollectionRepository featuredCollectionRepository;

    public FeaturedCollectionExpirationJob(FeaturedCollectionRepository featuredCollectionRepository) {
        this.featuredCollectionRepository = featuredCollectionRepository;
    }

    @Scheduled(fixedDelayString = "${featured.expiration-check-delay-ms:21600000}")
    @Transactional
    public void deactivateExpiredCollections() {
        featuredCollectionRepository.deactivateExpired(OffsetDateTime.now());
    }
}
