package com.company.documentai.web.model;

import com.company.documentai.domain.model.Document;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentDtoTest {

    @Test
    void shouldCreateDtoFromDomain() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Document document = new Document(
                id,
                "test.pdf",
                "application/pdf",
                createdAt,
                new byte[]{1, 2, 3}
        );

        DocumentDto dto = DocumentDto.fromDomain(document);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.fileName()).isEqualTo("test.pdf");
        assertThat(dto.fileType()).isEqualTo("application/pdf");
        assertThat(dto.createdAt()).isEqualTo(createdAt);
    }
}