package com.galeriafutbol.api.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galeriafutbol.api.dto.AlbumAdminRequest;
import com.galeriafutbol.api.dto.AlbumAdminResponse;
import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.dto.AlbumSearchFilter;
import com.galeriafutbol.api.exception.BadRequestException;
import com.galeriafutbol.api.exception.ConflictException;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.exception.UnauthorizedException;
import com.galeriafutbol.api.mapper.AlbumMapper;
import com.galeriafutbol.api.model.Album;
import com.galeriafutbol.api.model.AlbumStatus;
import com.galeriafutbol.api.model.Category;
import com.galeriafutbol.api.model.Image;
import com.galeriafutbol.api.model.User;
import com.galeriafutbol.api.repository.AlbumRepository;
import com.galeriafutbol.api.repository.AlbumImageCountProjection;
import com.galeriafutbol.api.repository.CategoryRepository;
import com.galeriafutbol.api.repository.ImageRepository;
import com.galeriafutbol.api.repository.UserRepository;
import com.galeriafutbol.api.service.AlbumService;
import com.galeriafutbol.api.service.ImageStorageService;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    private final CategoryRepository categoryRepository;

    private final ImageStorageService imageStorageService;
    private final AlbumMapper albumMapper;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public AlbumServiceImpl(AlbumRepository albumRepository,
            CategoryRepository categoryRepository,
            ImageStorageService imageStorageService,
            AlbumMapper albumMapper,
            UserRepository userRepository,
            ImageRepository imageRepository) {
        this.albumRepository = albumRepository;
        this.categoryRepository = categoryRepository;
        this.imageStorageService = imageStorageService;
        this.albumMapper = albumMapper;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumPublicResponse getAlbumById(Long id) {
        Album album = findAlbumEntityById(id);
        return albumMapper.toPublicResponse(album);
    }

    private Album findAlbumEntityById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumPublicResponse> searchAlbums(AlbumSearchFilter filter, Pageable pageable) {
        return searchAlbumsInternal(filter, pageable, AlbumStatus.PUBLISHED)
                .map(albumMapper::toPublicResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumAdminResponse> searchAlbumsForAdmin(AlbumSearchFilter filter, Pageable pageable,
            AlbumStatus status) {
        Page<Album> albumPage = searchAlbumsInternal(filter, pageable, status);

        List<Long> albumIds = albumPage.getContent().stream().map(Album::getId).toList();
        Map<Long, Long> imageCountByAlbumId = new HashMap<>();

        if (!albumIds.isEmpty()) {
            for (AlbumImageCountProjection row : imageRepository.countByAlbumIds(albumIds)) {
                imageCountByAlbumId.put(row.getAlbumId(), row.getTotalImages());
            }
        }

        return albumPage.map(album -> {
            AlbumAdminResponse dto = albumMapper.toAdminResponse(album);
            dto.setImageCount(imageCountByAlbumId.getOrDefault(album.getId(), 0L));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumAdminResponse getAlbumForAdminById(Long id) {
        Album album = findAlbumEntityById(id);
        AlbumAdminResponse dto = albumMapper.toAdminResponse(album);
        dto.setImageCount(imageRepository.countByAlbumIds(List.of(id)).stream()
                .findFirst()
                .map(AlbumImageCountProjection::getTotalImages)
                .orElse(0L));
        return dto;
    }

    private Page<Album> searchAlbumsInternal(AlbumSearchFilter filter, Pageable pageable, AlbumStatus status) {
        String query = (filter.getQ() == null || filter.getQ().isBlank()) ? null : filter.getQ().toLowerCase();
        String categoryCode = (filter.getCategoryCode() == null || filter.getCategoryCode().isBlank())
                ? null
                : filter.getCategoryCode();

        if (status == null) {
            return albumRepository.searchAlbumsWithoutStatus(
                    query,
                    filter.getTeamType(),
                    categoryCode,
                    filter.getSeasonStart(),
                    filter.getKids(),
                    filter.getWomen(),
                    filter.getGoalkeeper(),
                    filter.getTraining(),
                    filter.getClassic(),
                    filter.getRetro(),
                    pageable);
        }

        return albumRepository.searchAlbums(
                status,
                query,
                filter.getTeamType(),
                categoryCode,
                filter.getSeasonStart(),
                filter.getKids(),
                filter.getWomen(),
                filter.getGoalkeeper(),
                filter.getTraining(),
                filter.getClassic(),
                filter.getRetro(),
                pageable);
    }

    @Override
    @Transactional
    public AlbumAdminResponse createDraft() {
        int currentYear = OffsetDateTime.now().getYear();
        User currentUser = requireCurrentUser();
        Category defaultCategory = categoryRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ConflictException(
                        "No se puede crear borrador: no hay categorías configuradas"));

        for (int attempt = 0; attempt < 3; attempt++) {
            int seasonStart = currentYear + attempt;
            Album album = new Album();
            album.setTitle("Draft-" + UUID.randomUUID());
            album.setSeasonStart(seasonStart);
            album.setSeasonLabel(generateSeasonLabel(seasonStart));
            album.setCategory(defaultCategory);
            album.setCreatedBy(currentUser);

            try {
                Album saved = albumRepository.saveAndFlush(album);
                return albumMapper.toAdminResponse(saved);
            } catch (DataAccessException ex) {
                if (attempt == 2) {
                    throw new ConflictException("No se pudo crear un borrador por conflicto de datos");
                }
            }
        }

        throw new ConflictException("No se pudo crear un borrador por conflicto de datos");
    }

    @Override
    @Transactional
    public AlbumAdminResponse createAlbum(AlbumAdminRequest request) {
        Category category = findCategoryByCode(request.getCategoryCode());

        Album album = new Album();
        applyAdminRequestToAlbum(album, request, category);
        album.setCreatedBy(requireCurrentUser());

        Album saved = albumRepository.save(album);
        return albumMapper.toAdminResponse(saved);
    }

    @Override
    @Transactional
    public AlbumAdminResponse updateAlbum(Long id, AlbumAdminRequest request) {
        Album album = findAlbumEntityById(id);
        String oldThumbnail = album.getThumbnail();
        Category category = null;

        if (request.getCategoryCode() != null && !request.getCategoryCode().isBlank()) {
            category = findCategoryByCode(request.getCategoryCode());
        } else {
            category = album.getCategory();
        }

        applyAdminRequestToAlbum(album, request, category);
        album.setUpdatedBy(requireCurrentUser());

        deleteOldThumbnailIfUnused(id, oldThumbnail, request.getThumbnail());

        Album saved = albumRepository.save(album);
        return albumMapper.toAdminResponse(saved);
    }

    @Override
    @Transactional
    public void deleteAlbum(Long id) {
        Album album = findAlbumEntityById(id);
        String thumbnail = album.getThumbnail();

        if (thumbnail != null && !thumbnail.isBlank()) {
            imageStorageService.delete(thumbnail);
        }

        if (album.getImages() != null) {
            for (Image image : album.getImages()) {
                if (image.getUrl() != null && !image.getUrl().isBlank()) {
                    imageStorageService.delete(image.getUrl());
                }
            }
        }

        albumRepository.delete(album);
    }

    @Override
    @Transactional
    public AlbumAdminResponse publishAlbum(Long id, AlbumAdminRequest request) {
        if (request.getCategoryCode() == null || request.getCategoryCode().isBlank()) {
            throw new BadRequestException("La categoría es requerida para publicar un álbum");
        }

        Album album = findAlbumEntityById(id);
        String oldThumbnail = album.getThumbnail();
        Category category = findCategoryByCode(request.getCategoryCode());

        applyAdminRequestToAlbum(album, request, category);
        album.setStatus(AlbumStatus.PUBLISHED);
        album.setUpdatedBy(requireCurrentUser());

        deleteOldThumbnailIfUnused(id, oldThumbnail, request.getThumbnail());

        Album saved = albumRepository.save(album);
        return albumMapper.toAdminResponse(saved);
    }

    private void deleteOldThumbnailIfUnused(Long albumId, String oldThumbnail, String newThumbnail) {
        if (oldThumbnail == null || oldThumbnail.isBlank()) {
            return;
        }

        if (newThumbnail != null && oldThumbnail.equals(newThumbnail)) {
            return;
        }

        boolean stillReferencedByAlbumImages = imageRepository.existsByAlbumIdAndUrl(albumId, oldThumbnail);
        if (!stillReferencedByAlbumImages) {
            imageStorageService.delete(oldThumbnail);
        }
    }

    private Category findCategoryByCode(String code) {
        return categoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + code));
    }

    private void applyAdminRequestToAlbum(Album album, AlbumAdminRequest request, Category category) {
        album.setTitle(request.getTitle());
        album.setSeasonStart(request.getSeasonStart());

        if (request.getSeasonLabel() != null && !request.getSeasonLabel().isBlank()) {
            album.setSeasonLabel(request.getSeasonLabel());
        } else {
            album.setSeasonLabel(generateSeasonLabel(request.getSeasonStart()));
        }

        album.setCategory(category);
        album.setThumbnail(request.getThumbnail());
        album.setDescription(request.getDescription());

        album.setKids(Boolean.TRUE.equals(request.getKids()));
        album.setWomen(Boolean.TRUE.equals(request.getWomen()));
        album.setGoalkeeper(Boolean.TRUE.equals(request.getGoalkeeper()));
        album.setTraining(Boolean.TRUE.equals(request.getTraining()));
        album.setClassic(Boolean.TRUE.equals(request.getClassic()));
        album.setRetro(Boolean.TRUE.equals(request.getRetro()));

        if (request.getStatus() != null) {
            album.setStatus(request.getStatus());
        }
    }

    private String generateSeasonLabel(Integer seasonStart) {
        int nextYear = seasonStart + 1;
        return seasonStart + "-" + nextYear;
    }

    private Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }

    private User requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> new UnauthorizedException(
                        "No se pudo resolver el usuario autenticado para esta operación"));
    }

}
