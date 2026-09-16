package com.company.documentai.domain.service;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.infrastructure.service.DocumentTextExtractorImpl;
import com.company.documentai.infrastructure.service.FileServiceInfrastructure;
import com.company.documentai.infrastructure.service.OllamaClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
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

    @Mock
    private DocumentTextExtractorImpl textExtractor;

    @Mock
    private OllamaClient ollamaClient;

    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("Should save a document")
    void shouldSaveDocument() {

        final byte[] content =
                "test data".getBytes(StandardCharsets.UTF_8);

        final DocumentToSave documentToSave =
                new DocumentToSave(
                        "test.txt",
                        "text/plain",
                        Instant.now(),
                        content
                );

        final Document savedDocument =
                new Document(
                        UUID.randomUUID(),
                        "test.txt",
                        "text/plain",
                        documentToSave.createdAt(),
                        content
                );

        when(infrastructure.saveFile(any(DocumentToSave.class)))
                .thenReturn(savedDocument);

        final Document result =
                fileService.saveDocument(documentToSave);

        assertThat(result).isEqualTo(savedDocument);
        verify(infrastructure).saveFile(documentToSave);
    }

    @Test
    @DisplayName("Should throw exception when infrastructure fails")
    void shouldThrowExceptionWhenInfrastructureFails() {

        final byte[] content =
                "test data".getBytes(StandardCharsets.UTF_8);

        final DocumentToSave documentToSave =
                new DocumentToSave(
                        "test.txt",
                        "text/plain",
                        Instant.now(),
                        content
                );

        when(infrastructure.saveFile(any(DocumentToSave.class)))
                .thenThrow(new RuntimeException("Database down"));

        assertThatThrownBy(
                () -> fileService.saveDocument(documentToSave)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database down");
    }

    @Test
    @DisplayName("Should get all documents")
    void shouldGetAllDocuments() {

        final Document document =
                new Document(
                        UUID.randomUUID(),
                        "test.txt",
                        "text/plain",
                        Instant.now(),
                        "test data".getBytes(StandardCharsets.UTF_8)
                );

        when(infrastructure.getAllDocuments())
                .thenReturn(List.of(document));

        final List<Document> result =
                fileService.getAllDocuments();

        assertThat(result)
                .containsExactly(document);

        verify(infrastructure).getAllDocuments();
    }

    @Test
    @DisplayName("Should get document by id")
    void shouldGetDocumentById() {

        final UUID id = UUID.randomUUID();

        final Document document =
                new Document(
                        id,
                        "test.txt",
                        "text/plain",
                        Instant.now(),
                        "test data".getBytes(StandardCharsets.UTF_8)
                );

        when(infrastructure.getDocumentById(id))
                .thenReturn(document);

        final Document result =
                fileService.getDocumentById(id);

        assertThat(result).isEqualTo(document);
        verify(infrastructure).getDocumentById(id);
    }

    @Test
    @DisplayName("Should delete document")
    void shouldDeleteDocument() {

        final UUID id = UUID.randomUUID();

        fileService.deleteDocument(id);

        verify(infrastructure).deleteDocument(id);
    }

    @Test
    @DisplayName("Should summarize document")
    void shouldSummarizeDocument() {

        final UUID id = UUID.randomUUID();

        final byte[] content =
                "This is document content."
                        .getBytes(StandardCharsets.UTF_8);

        final Document document =
                new Document(
                        id,
                        "test.txt",
                        "text/plain",
                        Instant.now(),
                        content
                );

        final String extractedText =
                "This is document content.";

        final String summary =
                "Short summary.";

        when(infrastructure.getDocumentById(id))
                .thenReturn(document);

        when(textExtractor.extract(
                document.fileType(),
                document.content()
        ))
                .thenReturn(extractedText);

        when(ollamaClient.summarize(extractedText))
                .thenReturn(summary);

        final String result =
                fileService.summarizeDocument(id);

        assertThat(result).isEqualTo(summary);

        verify(infrastructure).getDocumentById(id);
        verify(textExtractor).extract(
                document.fileType(),
                document.content()
        );
        verify(ollamaClient).summarize(extractedText);
    }
}