package com.company.documentai.infrastructure.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository repository;

    @Test
    @DisplayName("Should save document")
    void shouldSaveDocument() {

        final byte[] content =
                "Hello world".getBytes(StandardCharsets.UTF_8);

        final DocumentEntity entity =
                DocumentEntity.builder()
                        .id(UUID.randomUUID())
                        .fileName("test.txt")
                        .fileType("text/plain")
                        .createdAt(Instant.now())
                        .content(content)
                        .build();

        final DocumentEntity saved =
                repository.save(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFileName()).isEqualTo("test.txt");
        assertThat(saved.getFileType()).isEqualTo("text/plain");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("Should find document by id")
    void shouldFindDocumentById() {

        final byte[] content =
                "content".getBytes(StandardCharsets.UTF_8);

        final DocumentEntity saved =
                repository.save(
                        DocumentEntity.builder()
                                .id(UUID.randomUUID())
                                .fileName("test.txt")
                                .fileType("text/plain")
                                .createdAt(Instant.now())
                                .content(content)
                                .build()
                );

        final Optional<DocumentEntity> result =
                repository.findById(saved.getId());

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(doc -> {
                    assertThat(doc.getFileName())
                            .isEqualTo("test.txt");
                    assertThat(doc.getFileType())
                            .isEqualTo("text/plain");
                    assertThat(doc.getCreatedAt())
                            .isNotNull();
                    assertThat(doc.getContent())
                            .isEqualTo(content);
                });
    }

    @Test
    @DisplayName("Should delete document")
    void shouldDeleteDocument() {

        final byte[] content =
                "to be deleted".getBytes(StandardCharsets.UTF_8);

        final DocumentEntity saved =
                repository.save(
                        DocumentEntity.builder()
                                .id(UUID.randomUUID())
                                .fileName("test.txt")
                                .fileType("text/plain")
                                .createdAt(Instant.now())
                                .content(content)
                                .build()
                );

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId()))
                .isEmpty();
    }
}
