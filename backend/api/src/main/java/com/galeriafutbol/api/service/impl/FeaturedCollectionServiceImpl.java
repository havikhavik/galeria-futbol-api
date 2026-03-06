package com.galeriafutbol.api.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galeriafutbol.api.dto.FeaturedCollectionAdminRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionAdminResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionPartialRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionWithAlbumsResponse;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.mapper.FeaturedCollectionMapper;
import com.galeriafutbol.api.model.FeaturedCollection;
import com.galeriafutbol.api.model.User;
import com.galeriafutbol.api.repository.FeaturedCollectionRepository;
import com.galeriafutbol.api.repository.UserRepository;
import com.galeriafutbol.api.service.FeaturedCollectionService;
import com.galeriafutbol.api.service.ImageStorageService;

@Service
public class FeaturedCollectionServiceImpl implements FeaturedCollectionService {

    private final FeaturedCollectionRepository featuredCollectionRepository;
    private final FeaturedCollectionMapper featuredCollectionMapper;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    public FeaturedCollectionServiceImpl(FeaturedCollectionRepository featuredCollectionRepository,
            FeaturedCollectionMapper featuredCollectionMapper,
            UserRepository userRepository,
            ImageStorageService imageStorageService) {
        this.featuredCollectionRepository = featuredCollectionRepository;
        this.featuredCollectionMapper = featuredCollectionMapper;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedCollectionResponse> getAllActive() {
        return featuredCollectionRepository.findAllActive()
                .stream()
                .map(featuredCollectionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FeaturedCollectionWithAlbumsResponse getBySlug(String slug) {
        FeaturedCollection collection = featuredCollectionRepository.findBySlug(slug);
        if (collection == null) {
            throw new ResourceNotFoundException("Promoción no encontrada: " + slug);
        }
        return featuredCollectionMapper.toResponseWithAlbums(collection);
    }

    @Override
    @Transactional
    public FeaturedCollectionAdminResponse createDraft() {
        FeaturedCollection collection = new FeaturedCollection();
        long timestamp = System.currentTimeMillis();
        collection.setSlug("draft-" + timestamp);
        collection.setTitle("Draft");
        collection.setDescription(null);
        collection.setStartDate(OffsetDateTime.now());
        collection.setEndDate(OffsetDateTime.now().plusDays(1));
        collection.setActive(false);
        collection.setPriority(0);
        collection.setBannerImage("placeholder");
        collection.setCreatedBy(getCurrentUser().orElse(null));
        collection.setCreatedAt(OffsetDateTime.now());
        collection.setUpdatedAt(OffsetDateTime.now());

        FeaturedCollection saved = featuredCollectionRepository.save(collection);
        return featuredCollectionMapper.toAdminResponse(saved);
    }

    @Override
    @Transactional
    public FeaturedCollectionAdminResponse createFeaturedCollection(FeaturedCollectionAdminRequest request) {
        FeaturedCollection collection = new FeaturedCollection();
        applyRequestToCollection(collection, request);
        collection.setCreatedBy(getCurrentUser().orElse(null));
        collection.setCreatedAt(OffsetDateTime.now());
        collection.setUpdatedAt(OffsetDateTime.now());

        FeaturedCollection saved = featuredCollectionRepository.save(collection);
        return featuredCollectionMapper.toAdminResponse(saved);
    }

    @Override
    @Transactional
    public FeaturedCollectionAdminResponse partialUpdate(Long id, FeaturedCollectionPartialRequest request) {
        FeaturedCollection collection = featuredCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + id));

        if (request.getBannerImage() != null) {
            String oldBanner = collection.getBannerImage();
            if (oldBanner != null && !oldBanner.isBlank() && !oldBanner.equals(request.getBannerImage())) {
                imageStorageService.delete(oldBanner);
            }
            collection.setBannerImage(request.getBannerImage());
        }

        if (request.getSlug() != null)
            collection.setSlug(request.getSlug());
        if (request.getTitle() != null)
            collection.setTitle(request.getTitle());
        if (request.getDescription() != null)
            collection.setDescription(request.getDescription());
        if (request.getPriority() != null)
            collection.setPriority(request.getPriority());
        if (request.getActive() != null)
            collection.setActive(request.getActive());

        collection.setUpdatedBy(getCurrentUser().orElse(null));
        collection.setUpdatedAt(OffsetDateTime.now());

        FeaturedCollection saved = featuredCollectionRepository.save(collection);
        return featuredCollectionMapper.toAdminResponse(saved);
    }

    @Override
    @Transactional
    public void deleteFeaturedCollection(Long id) {
        FeaturedCollection collection = featuredCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + id));

        if (collection.getBannerImage() != null && !collection.getBannerImage().isBlank()) {
            imageStorageService.delete(collection.getBannerImage());
        }

        featuredCollectionRepository.delete(collection);
    }

    private void applyRequestToCollection(FeaturedCollection collection, FeaturedCollectionAdminRequest request) {
        collection.setSlug(request.getSlug());
        collection.setTitle(request.getTitle());
        collection.setDescription(request.getDescription());
        collection.setStartDate(request.getStartDate());
        collection.setEndDate(request.getEndDate());
        collection.setActive(Boolean.TRUE.equals(request.getActive()));
        collection.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        collection.setBannerImage(request.getBannerImage());
    }

    private Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }
}
