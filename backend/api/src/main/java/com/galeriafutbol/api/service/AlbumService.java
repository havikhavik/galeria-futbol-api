package com.galeriafutbol.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.galeriafutbol.api.dto.AlbumAdminRequest;
import com.galeriafutbol.api.dto.AlbumAdminResponse;
import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.dto.AlbumSearchFilter;
import com.galeriafutbol.api.model.AlbumStatus;

public interface AlbumService {

    AlbumPublicResponse getAlbumById(Long id);

    Page<AlbumPublicResponse> searchAlbums(AlbumSearchFilter filter, Pageable pageable);

    Page<AlbumAdminResponse> searchAlbumsForAdmin(AlbumSearchFilter filter, Pageable pageable, AlbumStatus status);

    AlbumAdminResponse getAlbumForAdminById(Long id);

    AlbumAdminResponse createDraft();

    AlbumAdminResponse createAlbum(AlbumAdminRequest request);

    AlbumAdminResponse updateAlbum(Long id, AlbumAdminRequest request);

    void deleteAlbum(Long id);

    AlbumAdminResponse publishAlbum(Long id, AlbumAdminRequest request);
}
