package com.galeriafutbol.api.service;

import java.io.InputStream;

public interface ImageStorageService {

    String upload(InputStream inputStream, long contentLength, String contentType, String keyHint);

    void delete(String urlOrKey);
}
