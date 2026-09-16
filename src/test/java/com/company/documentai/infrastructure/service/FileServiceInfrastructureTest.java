package com.company.documentai.infrastructure.service;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.infrastructure.database.DocumentEntity;
import com.company.documentai.infrastructure.database.DocumentRepository;
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
class FileServiceInfrastructureTest {

    @Mock
    private DocumentRepository repository;

    @InjectMocks
    private FileServiceInfrastructure infrastructure;

    @Test
    @DisplayName("Should save document using repository")
    void shouldSaveDocument() {

        final Instant now = Instant.now();

        final byte[] content =
                "Hello world".getBytes(StandardCharsets.UTF_8);

        final DocumentToSave documentToSave =
                new DocumentToSave(
                        "test.txt",
                        "text/plain",
                        now,
                        content
                );

        final DocumentEntity savedEntity =
                DocumentEntity.builder()
                        .id(UUID.randomUUID())
                        .fileName("test.txt")
                        .fileType("text/plain")
                        .content(content)
                        .createdAt(now)
                        .build();

        when(repository.save(any(DocumentEntity.class)))
                .thenReturn(savedEntity);

        final Document result =
                infrastructure.saveFile(documentToSave);

        assertThat(result.id())
                .isEqualTo(savedEntity.getId());

        assertThat(result.fileName())
                .isEqualTo("test.txt");

        assertThat(result.fileType())
                .isEqualTo("text/plain");

        assertThat(result.content())
                .containsExactly(content);

        assertThat(result.createdAt())
                .isEqualTo(now);

        verify(repository)
                .save(any(DocumentEntity.class));
    }

    @Test
    @DisplayName("Should propagate repository exception")
    void shouldPropagateRepositoryException() {

        final byte[] content =
                "Hello world".getBytes(StandardCharsets.UTF_8);

        final DocumentToSave documentToSave =
                new DocumentToSave(
                        "test.txt",
                        "text/plain",
                        Instant.now(),
                        content
                );

        when(repository.save(any(DocumentEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(
                () -> infrastructure.saveFile(documentToSave)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(repository)
                .save(any(DocumentEntity.class));
    }

    @Test
    @DisplayName("Should preserve binary document content")
    void shouldPreserveBinaryDocumentContent() {

        final byte[] pdfContent = new byte[] {
                0x25, 0x50, 0x44, 0x46, 0x2D,
                0x31, 0x2E, 0x37, 0x00, 0x01,
                (byte) 0xFF, (byte) 0xFE
        };

        final DocumentToSave documentToSave =
                new DocumentToSave(
                        "test.pdf",
                        "application/pdf",
                        Instant.now(),
                        pdfContent
                );

        final DocumentEntity savedEntity =
                DocumentEntity.builder()
                        .id(UUID.randomUUID())
                        .fileName("test.pdf")
                        .fileType("application/pdf")
                        .content(pdfContent)
                        .createdAt(documentToSave.createdAt())
                        .build();

        when(repository.save(any(DocumentEntity.class)))
                .thenReturn(savedEntity);

        final Document result =
                infrastructure.saveFile(documentToSave);

        assertThat(result.content())
                .containsExactly(pdfContent);

        verify(repository)
                .save(any(DocumentEntity.class));
    }
}