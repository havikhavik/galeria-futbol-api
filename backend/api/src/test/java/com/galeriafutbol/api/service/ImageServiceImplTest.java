package com.galeriafutbol.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.galeriafutbol.api.dto.ImageAdminRequest;
import com.galeriafutbol.api.dto.ImageResponse;
import com.galeriafutbol.api.exception.ResourceNotFoundException;
import com.galeriafutbol.api.mapper.ImageMapper;
import com.galeriafutbol.api.model.Album;
import com.galeriafutbol.api.model.Image;
import com.galeriafutbol.api.repository.AlbumRepository;
import com.galeriafutbol.api.repository.ImageRepository;
import com.galeriafutbol.api.service.impl.ImageServiceImpl;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @Mock
    private ImageMapper imageMapper;

    @InjectMocks
    private ImageServiceImpl imageService;

    private Album album;

    @BeforeEach
    void setUp() {
        album = new Album();
        album.setId(1L);
    }

    @Test
    void replaceImagesForAlbum_throwsWhenAlbumNotFound() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imageService.replaceImagesForAlbum(1L, Collections.emptyList()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Álbum no encontrado");
    }

    @Test
    void replaceImagesForAlbum_createsNewImagesWhenNoExisting() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(imageRepository.findByAlbumIdOrderByPositionAsc(1L)).thenReturn(Collections.emptyList());

        Image saved1 = new Image();
        saved1.setId(10L);
        Image saved2 = new Image();
        saved2.setId(11L);

        when(imageRepository.save(any(Image.class))).thenReturn(saved1, saved2);

        ImageResponse response1 = new ImageResponse();
        response1.setId(10L);
        ImageResponse response2 = new ImageResponse();
        response2.setId(11L);
        when(imageMapper.toResponse(any(Image.class))).thenReturn(response1, response2);

        ImageAdminRequest req1 = new ImageAdminRequest();
        req1.setUrl("url1");
        req1.setPosition(1);
        req1.setPrimary(true);

        ImageAdminRequest req2 = new ImageAdminRequest();
        req2.setUrl("url2");
        req2.setPosition(2);
        req2.setPrimary(false);

        List<ImageResponse> result = imageService.replaceImagesForAlbum(1L, Arrays.asList(req1, req2));

        assertThat(result).hasSize(2);
        verify(imageRepository).findByAlbumIdOrderByPositionAsc(1L);
    }

    @Test
    void replaceImagesForAlbum_updatesExistingAndDeletesRemoved() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        Image existing1 = new Image();
        existing1.setId(100L);
        existing1.setAlbum(album);

        Image existing2 = new Image();
        existing2.setId(101L);
        existing2.setAlbum(album);
        existing2.setUrl("https://cdn.example.com/albums/1/old.png");

        when(imageRepository.findByAlbumIdOrderByPositionAsc(1L))
                .thenReturn(Arrays.asList(existing1, existing2));

        ImageAdminRequest keepAndUpdate = new ImageAdminRequest();
        keepAndUpdate.setId(100L);
        keepAndUpdate.setUrl("new-url");
        keepAndUpdate.setPosition(1);
        keepAndUpdate.setPrimary(true);

        ImageAdminRequest newImageReq = new ImageAdminRequest();
        newImageReq.setUrl("new2");
        newImageReq.setPosition(2);
        newImageReq.setPrimary(false);

        Image savedUpdated = new Image();
        savedUpdated.setId(100L);

        Image savedNew = new Image();
        savedNew.setId(200L);

        when(imageRepository.save(any(Image.class))).thenReturn(savedUpdated, savedNew);

        ImageResponse response1 = new ImageResponse();
        response1.setId(100L);
        ImageResponse response2 = new ImageResponse();
        response2.setId(200L);
        when(imageMapper.toResponse(any(Image.class))).thenReturn(response1, response2);

        List<ImageResponse> result = imageService.replaceImagesForAlbum(1L, Arrays.asList(keepAndUpdate, newImageReq));

        assertThat(result).hasSize(2);
        // existing2 (id=101) no viene en la lista, debe ser eliminado en la BD
        verify(imageRepository).deleteAll(any());
        verify(imageStorageService).delete("https://cdn.example.com/albums/1/old.png");
    }
}
