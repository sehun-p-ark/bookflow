package com.psh.bookflow.controller;

import com.psh.bookflow.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping({"/images", "/image", "", "/"})
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.storeImage(file);
        return Map.of("url", url);
    }
}