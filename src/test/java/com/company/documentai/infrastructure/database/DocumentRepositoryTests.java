package com.company.documentai.infrastructure.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DocumentRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2");

    @Autowired
    private DocumentRepository repository;

    @Test
    @DisplayName("ska kunna spara dokument")
    void shouldSaveDocument() {
        DocumentEntity entity = DocumentEntity.builder()
                .filename("test.txt")
                .fileType("text/plain")
                .content("Hello world")
                .build();

        DocumentEntity saved = repository.save(entity);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("ska kunna hitta dokument via id")
    void shouldFindDocumentById() {
        DocumentEntity saved = repository.save(DocumentEntity.builder()
                .filename("find.txt")
                .fileType("text/plain")
                .content("content")
                .build());

        Optional<DocumentEntity> result = repository.findById(saved.getId());

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(doc ->
                        assertThat(doc.getFilename()).isEqualTo("find.txt"));
    }

    @Test
    @DisplayName("ska kunna ta bort dokument")
    void shouldDeleteDocument() {
        DocumentEntity saved = repository.save(DocumentEntity.builder()
                .filename("delete.txt")
                .fileType("text/plain")
                .content("to be deleted")
                .build());

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
