package com.company.documentai.infrastructure.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

//TODO funkar inte - saknar filnamn
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DocumentRepositoryTests {

    @Autowired
    private DocumentRepository repository;

    @Test
    @DisplayName("Should save document")
    void shouldSaveDocument() {
        //TODO final på alla variabler
        byte[] content = "Hello world".getBytes(StandardCharsets.UTF_8);

        DocumentEntity entity = DocumentEntity.builder()
                .id(UUID.randomUUID())
                .fileType("text/plain")
                .content(content)
                .build();

        DocumentEntity saved = repository.save(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFileType()).isEqualTo("text/plain");
        assertThat(saved.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("Should find document by id")
    void shouldFindDocumentById() {
        byte[] content = "content".getBytes(StandardCharsets.UTF_8);

        DocumentEntity saved = repository.save(
                DocumentEntity.builder()
                        .id(UUID.randomUUID())
                        .fileType("text/plain")
                        .content(content)
                        .build()
        );

        Optional<DocumentEntity> result = repository.findById(saved.getId());

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(doc -> {
                    assertThat(doc.getFileType()).isEqualTo("text/plain");
                    assertThat(doc.getContent()).isEqualTo(content);
                });
    }

    @Test
    @DisplayName("Should delete document")
    void shouldDeleteDocument() {
        byte[] content = "to be deleted".getBytes(StandardCharsets.UTF_8);

        DocumentEntity saved = repository.save(
                DocumentEntity.builder()
                        .id(UUID.randomUUID())
                        .fileType("text/plain")
                        .content(content)
                        .build()
        );

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
