package com.company.documentai.infrastructure.database;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentEntityTest {

    @Test
    @DisplayName("Should map correctly from DocumentToSave to entity")
    void shouldMapFromDomain() {

        Instant now = Instant.now();

        byte[] content = "world class code"
                .getBytes(StandardCharsets.UTF_8);

        DocumentToSave documentToSave = new DocumentToSave(
                "test.pdf",
                "application/pdf",
                now,
                content
        );

        DocumentEntity entity =
                DocumentEntity.fromDomain(documentToSave);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFileName()).isEqualTo("test.pdf");
        assertThat(entity.getFileType()).isEqualTo("application/pdf");
        assertThat(entity.getContent())
                .containsExactly(content);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should map correctly from entity to domain")
    void shouldMapToDomain() {

        final UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        byte[] content = "binary data test"
                .getBytes(StandardCharsets.UTF_8);

        DocumentEntity entity = new DocumentEntity(
                id,
                "test.pdf",
                "application/octet-stream",
                content,
                now
        );

        Document document = entity.toDomain();

        assertThat(document.id()).isEqualTo(id);
        assertThat(document.fileName()).isEqualTo("test.pdf");
        assertThat(document.fileType())
                .isEqualTo("application/octet-stream");
        assertThat(document.content())
                .containsExactly(content);
        assertThat(document.createdAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should preserve binary content when mapping from domain")
    void shouldPreserveBinaryContentWhenMappingFromDomain() {

        byte[] content = new byte[] {
                0x25, 0x50, 0x44, 0x46, 0x2D,
                0x31, 0x2E, 0x37, 0x00, 0x01,
                (byte) 0xFF, (byte) 0xFE
        };

        DocumentToSave documentToSave = new DocumentToSave(
                "test.pdf",
                "application/pdf",
                Instant.now(),
                content
        );

        DocumentEntity entity =
                DocumentEntity.fromDomain(documentToSave);

        assertThat(entity.getContent())
                .containsExactly(content);
    }

    @Test
    @DisplayName("Should preserve binary content when mapping to domain")
    void shouldPreserveBinaryContentWhenMappingToDomain() {

        byte[] content = new byte[] {
                0x00,
                0x01,
                0x02,
                0x7F,
                (byte) 0x80,
                (byte) 0xFE,
                (byte) 0xFF
        };

        DocumentEntity entity = DocumentEntity.builder()
                .id(UUID.randomUUID())
                .fileName("test.pdf")
                .fileType("application/pdf")
                .createdAt(Instant.now())
                .content(content)
                .build();

        Document document = entity.toDomain();

        assertThat(document.content())
                .containsExactly(content);
    }

    @Test
    @DisplayName("Should preserve multiline text content")
    void shouldPreserveMultilineContent() {

        String text = """
                First line
                Second line
                Third line
                """;

        byte[] content = text.getBytes(StandardCharsets.UTF_8);

        DocumentEntity entity = DocumentEntity.builder()
                .id(UUID.randomUUID())
                .fileName("test.txt")
                .fileType("text/plain")
                .createdAt(Instant.now())
                .content(content)
                .build();

        Document document = entity.toDomain();

        assertThat(document.content())
                .containsExactly(content);
    }
}