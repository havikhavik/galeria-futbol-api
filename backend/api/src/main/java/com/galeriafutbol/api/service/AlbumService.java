package com.galeriafutbol.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.galeriafutbol.api.dto.AlbumAdminRequest;
import com.galeriafutbol.api.dto.AlbumAdminResponse;
import com.galeriafutbol.api.dto.AlbumPublicResponse;
import com.galeriafutbol.api.dto.AlbumSearchFilter;

public interface AlbumService {

    AlbumPublicResponse getAlbumById(Long id);

    Page<AlbumPublicResponse> searchAlbums(AlbumSearchFilter filter, Pageable pageable);

    AlbumAdminResponse createDraft();

    AlbumAdminResponse createAlbum(AlbumAdminRequest request);

    AlbumAdminResponse updateAlbum(Long id, AlbumAdminRequest request);

    void deleteAlbum(Long id);

    AlbumAdminResponse publishAlbum(Long id, AlbumAdminRequest request);
}
