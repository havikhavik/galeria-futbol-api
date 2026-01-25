package com.galeriafutbol.api.service.impl;

import java.io.InputStream;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.galeriafutbol.api.exception.BadRequestException;
import com.galeriafutbol.api.service.ImageStorageService;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Profile("!test")
public class R2ImageStorageService implements ImageStorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String publicBaseUrl;

    public R2ImageStorageService(
            @Value("${R2_BUCKET_NAME}") String bucketName,
            @Value("${R2_PUBLIC_BASE_URL}") String publicBaseUrl,
            @Value("${R2_ACCOUNT_ID}") String accountId,
            @Value("${R2_ACCESS_KEY_ID}") String accessKeyId,
            @Value("${R2_SECRET_ACCESS_KEY}") String secretKey) {

        this.bucketName = bucketName;
        this.publicBaseUrl = publicBaseUrl != null ? publicBaseUrl.replaceAll("/+$", "") : null;

        if (bucketName == null || accessKeyId == null || secretKey == null || accountId == null
                || this.publicBaseUrl == null) {
            throw new IllegalStateException("Faltan variables de entorno de configuración de R2");
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretKey);

        String endpoint = "https://" + accountId + ".r2.cloudflarestorage.com";

        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // región dummy requerida por el SDK
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpoint))
                .build();
    }

    @Override
    public String upload(InputStream inputStream, long contentLength, String contentType, String keyHint) {
        if (inputStream == null || contentLength <= 0) {
            throw new BadRequestException("Contenido de imagen inválido para subir a R2");
        }

        String key = keyHint;
        if (key == null || key.isBlank()) {
            throw new BadRequestException("Key de objeto inválida para R2");
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));

        return publicBaseUrl + "/" + key;
    }

    @Override
    public void delete(String urlOrKey) {
        if (urlOrKey == null || urlOrKey.isBlank()) {
            return;
        }

        String key;
        String base = publicBaseUrl + "/";
        if (urlOrKey.startsWith(base)) {
            key = urlOrKey.substring(base.length());
        } else if (!urlOrKey.contains("://")) {
            key = urlOrKey;
        } else {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}
