package com.company.documentai.web.controller;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.domain.service.FileService;
import com.company.documentai.web.mapper.DocumentMapper;
import com.company.documentai.web.model.DocumentDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "txt",
            "pdf",
            "doc",
            "docx"
    );

    private final FileService fileService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DocumentDto> uploadFile(
            @RequestParam("file") final MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        final String fileName = file.getOriginalFilename();

        if (!StringUtils.hasText(fileName) || !fileName.contains(".")) {
            return ResponseEntity.badRequest().build();
        }

        final String extension = fileName
                .substring(fileName.lastIndexOf('.') + 1)
                .toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return ResponseEntity.badRequest().build();
        }

        final DocumentToSave documentToSave =
                DocumentMapper.toDomain(file, fileName);

        final Document savedDocument =
                fileService.saveDocument(documentToSave);

        return ResponseEntity.ok(
                DocumentDto.fromDomain(savedDocument)
        );
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getDocuments() {

        final List<DocumentDto> documents =
                fileService.getAllDocuments()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        Document::createdAt
                                ).reversed()
                        )
                        .map(DocumentDto::fromDomain)
                        .toList();

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable final UUID id
    ) {

        try {
            final Document document =
                    fileService.getDocumentById(id);

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    document.fileType()
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\""
                                    + document.fileName()
                                    + "\""
                    )
                    .body(document.content());

        } catch (final EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable final UUID id
    ) {

        try {
            fileService.deleteDocument(id);
            return ResponseEntity.noContent().build();

        } catch (final EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<String> summarizeDocument(
            @PathVariable final UUID id
    ) {

        try {
            final String summary =
                    fileService.summarizeDocument(id);

            return ResponseEntity.ok(summary);

        } catch (final EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}