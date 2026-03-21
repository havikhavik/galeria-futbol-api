package com.galeriafutbol.api.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionAdminRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionAdminResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionPartialRequest;
import com.galeriafutbol.api.dto.FeaturedCollectionResponse;
import com.galeriafutbol.api.dto.FeaturedCollectionWithAlbumsResponse;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.mapper.FeaturedCollectionMapper;
import com.galeriafutbol.api.model.Album;
import com.galeriafutbol.api.model.FeaturedCollection;
import com.galeriafutbol.api.model.FeaturedCollectionAlbum;
import com.galeriafutbol.api.model.User;
import com.galeriafutbol.api.repository.AlbumRepository;
import com.galeriafutbol.api.repository.FeaturedCollectionAlbumRepository;
import com.galeriafutbol.api.repository.FeaturedCollectionRepository;
import com.galeriafutbol.api.repository.UserRepository;
import com.galeriafutbol.api.service.FeaturedCollectionService;
import com.galeriafutbol.api.service.ImageStorageService;

@Service
public class FeaturedCollectionServiceImpl implements FeaturedCollectionService {

    private final FeaturedCollectionRepository featuredCollectionRepository;
    private final FeaturedCollectionAlbumRepository featuredCollectionAlbumRepository;
    private final AlbumRepository albumRepository;
    private final FeaturedCollectionMapper featuredCollectionMapper;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern DUPLICATE_DASH = Pattern.compile("-+");

    public FeaturedCollectionServiceImpl(FeaturedCollectionRepository featuredCollectionRepository,
            FeaturedCollectionAlbumRepository featuredCollectionAlbumRepository,
            AlbumRepository albumRepository,
            FeaturedCollectionMapper featuredCollectionMapper,
            UserRepository userRepository,
            ImageStorageService imageStorageService) {
        this.featuredCollectionRepository = featuredCollectionRepository;
        this.featuredCollectionAlbumRepository = featuredCollectionAlbumRepository;
        this.albumRepository = albumRepository;
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
    public List<FeaturedCollectionAdminResponse> getAllForAdmin() {
        return featuredCollectionRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"))
                .stream()
                .map(featuredCollectionMapper::toAdminResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FeaturedCollectionAdminResponse getByIdForAdmin(Long id) {
        FeaturedCollection collection = featuredCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + id));
        return featuredCollectionMapper.toAdminResponse(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumPublicResponse> getAlbumsForAdmin(Long id) {
        FeaturedCollection collection = featuredCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + id));

        return featuredCollectionMapper.toResponseWithAlbums(collection).getAlbums();
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
        collection.setSlug(buildUniqueSlug(request.getTitle(), null));
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

        if (request.getTitle() != null) {
            collection.setTitle(request.getTitle());
            collection.setSlug(buildUniqueSlug(request.getTitle(), collection.getId()));
        }
        if (request.getDescription() != null)
            collection.setDescription(request.getDescription());
        if (request.getStartDate() != null)
            collection.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)
            collection.setEndDate(request.getEndDate());
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
    public void addAlbumToCollection(Long collectionId, Long albumId) {
        FeaturedCollection collection = featuredCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + collectionId));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado: " + albumId));

        boolean alreadyExists = featuredCollectionAlbumRepository
                .existsByFeaturedCollectionIdAndAlbumId(collectionId, albumId);
        if (alreadyExists) {
            return;
        }

        long existingCount = featuredCollectionAlbumRepository.countByFeaturedCollectionId(collectionId);

        FeaturedCollectionAlbum association = new FeaturedCollectionAlbum();
        association.setFeaturedCollection(collection);
        association.setAlbum(album);
        association.setDisplayOrder((int) existingCount + 1);

        featuredCollectionAlbumRepository.save(association);
    }

    @Override
    @Transactional
    public void removeAlbumFromCollection(Long collectionId, Long albumId) {
        featuredCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + collectionId));

        featuredCollectionAlbumRepository.deleteByFeaturedCollectionIdAndAlbumId(collectionId, albumId);

        List<FeaturedCollectionAlbum> associations = featuredCollectionAlbumRepository
                .findByFeaturedCollectionIdOrderByDisplayOrderAsc(collectionId);

        int order = 1;
        for (FeaturedCollectionAlbum association : associations) {
            association.setDisplayOrder(order++);
        }
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
        collection.setTitle(request.getTitle());
        collection.setDescription(request.getDescription());
        collection.setStartDate(request.getStartDate());
        collection.setEndDate(request.getEndDate());
        collection.setActive(Boolean.TRUE.equals(request.getActive()));
        collection.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        collection.setBannerImage(request.getBannerImage());
    }

    private String buildUniqueSlug(String title, Long existingCollectionId) {
        String baseSlug = slugify(title);
        if (baseSlug.isBlank()) {
            baseSlug = "coleccion";
        }

        String candidate = baseSlug;
        int suffix = 2;

        while (slugExistsForAnotherCollection(candidate, existingCollectionId)) {
            candidate = baseSlug + "-" + suffix;
            suffix++;
        }

        return candidate;
    }

    private boolean slugExistsForAnotherCollection(String slug, Long existingCollectionId) {
        if (existingCollectionId == null) {
            return featuredCollectionRepository.existsBySlug(slug);
        }
        return featuredCollectionRepository.existsBySlugAndIdNot(slug, existingCollectionId);
    }

    private String slugify(String input) {
        if (input == null) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String lower = withoutDiacritics.toLowerCase(Locale.ROOT).trim();
        String withDashes = WHITESPACE.matcher(lower).replaceAll("-");
        String clean = NON_LATIN.matcher(withDashes).replaceAll("");
        return DUPLICATE_DASH.matcher(clean).replaceAll("-").replaceAll("(^-+|-+$)", "");
    }

    private Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }
}
