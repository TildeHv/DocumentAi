package com.company.documentai.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.web.mapper.DocumentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

class DocumentMapperTest {

    @Test
    void shouldMapMultipartFileToDomain() throws IOException {

        final String fileName = "test-document.pdf";
        final String contentType = "application/pdf";
        final byte[] content = "Test document content".getBytes();

        final MultipartFile file =
                new MockMultipartFile(
                        "file",
                        fileName,
                        contentType,
                        content
                );

        final Instant before = Instant.now();

        final DocumentToSave result =
                DocumentMapper.toDomain(
                        file,
                        fileName
                );

        final Instant after = Instant.now();

        assertThat(result).isNotNull();
        assertThat(result.fileName())
                .isEqualTo(fileName);
        assertThat(result.fileType())
                .isEqualTo(contentType);
        assertThat(result.createdAt())
                .isBetween(before, after);
        assertThat(result.content())
                .isEqualTo(content);
    }

    @Test
    void shouldUseDefaultContentTypeWhenContentTypeIsNull()
            throws IOException {

        final String fileName = "test-document";
        final byte[] content = "Test document content".getBytes();

        final MultipartFile file =
                mock(MultipartFile.class);

        when(file.getContentType())
                .thenReturn(null);

        when(file.getBytes())
                .thenReturn(content);

        final DocumentToSave result =
                DocumentMapper.toDomain(
                        file,
                        fileName
                );

        assertThat(result.fileName())
                .isEqualTo(fileName);

        assertThat(result.fileType())
                .isEqualTo("application/octet-stream");

        assertThat(result.content())
                .isEqualTo(content);
    }

    @Test
    void shouldUseDefaultContentTypeWhenContentTypeIsBlank()
            throws IOException {

        final String fileName = "test-document";
        final byte[] content = "Test document content".getBytes();

        final MultipartFile file =
                mock(MultipartFile.class);

        when(file.getContentType())
                .thenReturn(" ");

        when(file.getBytes())
                .thenReturn(content);

        final DocumentToSave result =
                DocumentMapper.toDomain(
                        file,
                        fileName
                );

        assertThat(result.fileType())
                .isEqualTo("application/octet-stream");
    }

    @Test
    void shouldUseProvidedFileName() throws IOException {

        final String originalFileName = "original.pdf";
        final String providedFileName = "renamed-document.pdf";

        final MultipartFile file =
                new MockMultipartFile(
                        "file",
                        originalFileName,
                        "application/pdf",
                        "Test content".getBytes()
                );

        final DocumentToSave result =
                DocumentMapper.toDomain(
                        file,
                        providedFileName
                );

        assertThat(result.fileName())
                .isEqualTo(providedFileName);
    }
}