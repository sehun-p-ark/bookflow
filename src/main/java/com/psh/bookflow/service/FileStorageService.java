package com.psh.bookflow.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private final Path uploadRoot = Paths.get(System.getProperty("user.dir"), "uploads");

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String original = file.getOriginalFilename();
        String extension = extractExtension(original);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 이미지 확장자입니다.");
        }

        try {
            Files.createDirectories(uploadRoot);
            String filename = UUID.randomUUID() + "." + extension;
            Path destination = uploadRoot.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
        }
    }

    private static String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다.");
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}