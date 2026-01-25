package com.galeriafutbol.api;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import com.galeriafutbol.api.service.ImageStorageService;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@ActiveProfiles("test")
class ApiApplicationTests {

	@TestConfiguration
	static class TestStorageConfig {
		@Bean
		ImageStorageService imageStorageService() {
			return new ImageStorageService() {
				@Override
				public String upload(InputStream inputStream, long contentLength, String contentType, String keyHint) {
					return "https://example.invalid/test-image";
				}

				@Override
				public void delete(String urlOrKey) {
					// no-op for tests
				}
			};
		}
	}

	@Test
	void contextLoads() {
	}

}
