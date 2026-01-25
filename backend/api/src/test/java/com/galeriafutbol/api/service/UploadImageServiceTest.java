package com.galeriafutbol.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.galeriafutbol.api.dto.UploadImageResponse;
import com.galeriafutbol.api.exception.BadRequestException;
import com.galeriafutbol.api.service.impl.UploadImageServiceImpl;

@ExtendWith(MockitoExtension.class)
class UploadImageServiceTest {

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private UploadImageServiceImpl uploadImageService;

    @Test
    void uploadForAdmin_throwsWhenFileEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        assertThatThrownBy(() -> uploadImageService.uploadForAdmin(emptyFile, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El archivo de imagen es obligatorio");
    }

    @Test
    void uploadForAdmin_uploadsToStorageAndReturnsResponse() throws Exception {
        byte[] content = "dummy".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", content);

        when(imageStorageService.upload(any(), anyLong(), anyString(), anyString()))
                .thenReturn("https://cdn.example.com/albums/1/test.png");

        UploadImageResponse response = uploadImageService.uploadForAdmin(file, 1L);

        assertThat(response.getUrl()).contains("albums/1");
        verify(imageStorageService).upload(any(InputStream.class), anyLong(), anyString(), anyString());
    }

    @Test
    void uploadForAdmin_wrapsExceptionsFromStorage() throws Exception {
        byte[] content = "dummy".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", content);

        when(imageStorageService.upload(any(), anyLong(), anyString(), anyString()))
                .thenThrow(new RuntimeException("storage error"));

        assertThatThrownBy(() -> uploadImageService.uploadForAdmin(file, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("No se pudo subir la imagen a R2");
    }

    @Test
    void uploadForCategory_throwsWhenFileEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        assertThatThrownBy(() -> uploadImageService.uploadForCategory(emptyFile, "LA_LIGA"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("El archivo de imagen es obligatorio");
    }

    @Test
    void uploadForCategory_throwsWhenCategoryCodeMissing() {
        byte[] content = "dummy".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "logo.svg", "image/svg+xml", content);

        assertThatThrownBy(() -> uploadImageService.uploadForCategory(file, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("código de categoría es obligatorio");

        assertThatThrownBy(() -> uploadImageService.uploadForCategory(file, "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("código de categoría es obligatorio");
    }

    @Test
    void uploadForCategory_uploadsToCorrectPath() throws Exception {
        byte[] content = "dummy".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "logo.svg", "image/svg+xml", content);

        when(imageStorageService.upload(any(), anyLong(), anyString(), anyString()))
                .thenReturn("https://cdn.example.com/icons/categories/LA_LIGA/logo.svg");

        UploadImageResponse response = uploadImageService.uploadForCategory(file, "LA_LIGA");

        assertThat(response.getUrl()).contains("icons/categories/LA_LIGA");
        verify(imageStorageService).upload(any(InputStream.class), anyLong(), anyString(), anyString());
    }
}
