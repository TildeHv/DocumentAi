package com.company.documentai.infrastructure.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentTextExtractorImplTest {

    private DocumentTextExtractorImpl extractor;

    @BeforeEach
    void setUp() {
        extractor = new DocumentTextExtractorImpl();
    }

    @Test
    void shouldExtractTextFromPlainText() {

        final String input = "Detta är ett testdokument.";

        final String result =
                extractor.extract(
                        "text/plain",
                        input.getBytes(StandardCharsets.UTF_8));

        assertEquals(input, result);
    }

    @Test
    void shouldExtractTextFromPdf() throws IOException {

        final byte[] content =
                Files.readAllBytes(
                        Path.of(
                                "src/test/resources/testfiles/Studentspegeln 2025.pdf"));

        final String result =
                extractor.extract(
                        "application/pdf",
                        content);

        assertTrue(result.contains("Studentspegeln"));
    }

    @Test
    void shouldThrowExceptionForEmptyContent() {

        final IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                extractor.extract(
                                        "text/plain",
                                        new byte[0]));

        assertEquals(
                "Document content is empty",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullContent() {

        final IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                extractor.extract(
                                        "text/plain",
                                        null));

        assertEquals(
                "Document content is empty",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForUnsupportedDocumentType() {

        final IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                extractor.extract(
                                        "application/unknown",
                                        "test".getBytes(StandardCharsets.UTF_8)));

        assertEquals(
                "Unsupported document type: application/unknown",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidPdf() {

        final byte[] invalidPdf =
                "This is not a PDF".getBytes(StandardCharsets.UTF_8);

        final IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                extractor.extract(
                                        "application/pdf",
                                        invalidPdf));

        assertEquals(
                "Could not extract text from PDF",
                exception.getMessage());
    }
}