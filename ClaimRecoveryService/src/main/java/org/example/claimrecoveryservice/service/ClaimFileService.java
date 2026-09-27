package org.example.claimrecoveryservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ClaimFileService {

    @Value("${claims.upload-dir:uploads/claims}")
    private String uploadDir;

    public Mono<String> uploadFile(String claimId, FilePart file) {
        String originalName = file.filename();
        String extension = "";

        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            extension = originalName.substring(dot).toLowerCase();
        }

        if (!extension.matches("\\.(pdf|png|jpg|jpeg|docx)")) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF, PNG, JPG, JPEG, and DOCX files are allowed"
            ));
        }

        String filename = UUID.randomUUID() + extension;

        Path directory = Paths.get(uploadDir, claimId)
                .toAbsolutePath()
                .normalize();

        Path destination = directory.resolve(filename).normalize();

        if (!destination.startsWith(directory)) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file path"
            ));
        }

        return Mono.fromCallable(() -> {
                    Files.createDirectories(directory);
                    return destination;
                })
                .flatMap(path -> file.transferTo(path).thenReturn(filename));
    }

    public Mono<List<String>> listFiles(String claimId) {
        Path directory = Paths.get(uploadDir, claimId)
                .toAbsolutePath()
                .normalize();

        return Mono.fromCallable(() -> {
            if (!Files.exists(directory)) {
                return List.<String>of();
            }

            try (Stream<Path> files = Files.list(directory)) {
                return files
                        .filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .filter(name -> name.matches(
                                "[a-fA-F0-9-]+\\.(pdf|png|jpg|jpeg|docx)"
                        ))
                        .sorted()
                        .toList();
            }
        });
    }

    public Mono<Path> getFile(String claimId, String filename) {
        if (!filename.matches(
                "[a-fA-F0-9-]+\\.(pdf|png|jpg|jpeg|docx)"
        )) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid filename"
            ));
        }

        Path directory = Paths.get(uploadDir, claimId)
                .toAbsolutePath()
                .normalize();

        Path file = directory.resolve(filename)
                .normalize();

        if (!file.startsWith(directory)) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file path"
            ));
        }

        return Mono.fromCallable(() -> {
            if (!Files.exists(file) || !Files.isRegularFile(file)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "File not found"
                );
            }

            return file;
        });
    }
}