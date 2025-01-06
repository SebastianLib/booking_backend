package com.sebastian.service;

import com.sebastian.model.ImageData;
import com.sebastian.repository.ImageDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final String uploadDir = "uploads/";

    @Autowired
    private ImageDataRepository imageDataRepository;

    public String uploadImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generowanie unikalnej nazwy pliku
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        // Zapis pliku na dysk
        // Zapis pliku na dysk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Zapis informacji o pliku w bazie danych
        ImageData imageData = ImageData.builder()
                .name(originalFilename)
                .type(file.getContentType())
                .filePath(filePath.toString())
                .build();
        imageDataRepository.save(imageData);

        return uniqueFilename;
    }

    public byte[] downloadImage(String fileName) {
        // Wyszukiwanie obrazu w bazie danych
        Optional<ImageData> imageDataOptional = imageDataRepository.findByFilePathContaining(fileName);
        if (imageDataOptional.isPresent()) {
            try {
                Path filePath = Paths.get(imageDataOptional.get().getFilePath());
                return Files.readAllBytes(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Błąd podczas odczytu pliku: " + fileName, e);
            }
        } else {
            throw new RuntimeException("Obraz o podanej nazwie nie istnieje: " + fileName);
        }
    }
}