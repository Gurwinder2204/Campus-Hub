package com.campusstudyhub.config;

import com.campusstudyhub.util.FileStorageUtil;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Custom health indicator for local storage (Minio placeholder or local FS).
 */
@Component
public class StorageHealthIndicator implements HealthIndicator {

    private final String uploadDir;

    public StorageHealthIndicator() {
        // Default to 'uploads' if not injected (simplification for now)
        this.uploadDir = "uploads";
    }

    @Override
    public Health health() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            if (Files.isWritable(path)) {
                return Health.up()
                        .withDetail("directory", uploadDir)
                        .withDetail("writable", true)
                        .withDetail("absolutePath", path.toAbsolutePath().toString())
                        .build();
            } else {
                return Health.down()
                        .withDetail("directory", uploadDir)
                        .withDetail("reason", "Upload directory not writable")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("directory", uploadDir)
                    .build();
        }
    }
}
