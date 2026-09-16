package com.company.documentai.domain.service;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.infrastructure.service.FileServiceInfrastructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileServiceInfrastructure infrastructure;

    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("Should save a document")
    void shouldSaveDocument() {

        byte[] content = "test data".getBytes(StandardCharsets.UTF_8);

        DocumentToSave documentToSave = new DocumentToSave(
                "test.txt",
                "text/plain",
                Instant.now(),
                content
        );

        Document savedDocument = new Document(
                UUID.randomUUID(),
                "test.txt",
                "text/plain",
                documentToSave.createdAt(),
                content
        );

        when(infrastructure.saveFile(any(DocumentToSave.class)))
                .thenReturn(savedDocument);

        Document result = fileService.saveDocument(documentToSave);

        assertThat(result).isEqualTo(savedDocument);
        verify(infrastructure).saveFile(documentToSave);
    }

    @Test
    @DisplayName("Should throw exception when infrastructure fails")
    void shouldThrowExceptionWhenInfrastructureFails() {

        byte[] content = "test data".getBytes(StandardCharsets.UTF_8);

        DocumentToSave documentToSave = new DocumentToSave(
                "test.txt",
                "text/plain",
                Instant.now(),
                content
        );

        when(infrastructure.saveFile(any(DocumentToSave.class)))
                .thenThrow(new RuntimeException("Database down"));

        assertThatThrownBy(() -> fileService.saveDocument(documentToSave))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database down");
    }
}