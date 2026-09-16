package com.company.documentai.web.controller;

import com.company.documentai.DocumentaiApplication;
import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.domain.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = DocumentaiApplication.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private static final String UPLOAD_URL =
            "/api/v1/documents/upload";

    @Test
    @DisplayName("Ska lyckas ladda upp en textfil")
    void shouldUploadTextFileSuccessfully() throws Exception {

        final byte[] fileBytes =
                "Hello, World!".getBytes();

        final Document savedDocument = new Document(
                UUID.randomUUID(),
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                Instant.now(),
                fileBytes
        );

        when(fileService.saveDocument(any(DocumentToSave.class)))
                .thenReturn(savedDocument);

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileBytes
        );

        mockMvc.perform(
                        multipart(UPLOAD_URL)
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.fileName")
                                .value("test.txt")
                )
                .andExpect(
                        jsonPath("$.contentType")
                                .value(MediaType.TEXT_PLAIN_VALUE)
                );

        verify(fileService)
                .saveDocument(any(DocumentToSave.class));
    }

    @Test
    @DisplayName("Ska lyckas ladda upp Studentspegeln 2025 som PDF")
    void shouldUploadStudentspegelnPdfSuccessfully()
            throws Exception {

        final InputStream inputStream = getClass()
                .getResourceAsStream(
                        "/testfiles/Studentspegeln 2025.pdf"
                );

        assertNotNull(
                inputStream,
                "PDF-filen kunde inte hittas"
        );

        final byte[] pdfBytes =
                inputStream.readAllBytes();

        final Document savedDocument = new Document(
                UUID.randomUUID(),
                "Studentspegeln 2025.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                Instant.now(),
                pdfBytes
        );

        when(fileService.saveDocument(any(DocumentToSave.class)))
                .thenReturn(savedDocument);

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "Studentspegeln 2025.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                pdfBytes
        );

        mockMvc.perform(
                        multipart(UPLOAD_URL)
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.fileName")
                                .value("Studentspegeln 2025.pdf")
                )
                .andExpect(
                        jsonPath("$.contentType")
                                .value(MediaType.APPLICATION_PDF_VALUE)
                );

        verify(fileService)
                .saveDocument(any(DocumentToSave.class));
    }

    @Test
    @DisplayName("Ska neka JSON-fil")
    void shouldRejectJsonFile() throws Exception {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                "{}".getBytes()
        );

        mockMvc.perform(
                        multipart(UPLOAD_URL)
                                .file(file)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ska neka fil utan filnamn")
    void shouldRejectFileWithoutFilename() throws Exception {

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes()
        );

        mockMvc.perform(
                        multipart(UPLOAD_URL)
                                .file(file)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ska bevara PDF-filens binära innehåll")
    void shouldPreservePdfBytes() throws Exception {

        final InputStream inputStream = getClass()
                .getResourceAsStream(
                        "/testfiles/Studentspegeln 2025.pdf"
                );

        assertNotNull(
                inputStream,
                "PDF-filen kunde inte hittas"
        );

        final byte[] pdfBytes =
                inputStream.readAllBytes();

        when(fileService.saveDocument(any(DocumentToSave.class)))
                .thenAnswer(invocation -> {

                    final DocumentToSave documentToSave =
                            invocation.getArgument(0);

                    assertThat(documentToSave.content())
                            .containsExactly(pdfBytes);

                    return new Document(
                            UUID.randomUUID(),
                            documentToSave.fileName(),
                            documentToSave.fileType(),
                            documentToSave.createdAt(),
                            documentToSave.content()
                    );
                });

        final MockMultipartFile file = new MockMultipartFile(
                "file",
                "Studentspegeln 2025.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                pdfBytes
        );

        mockMvc.perform(
                        multipart(UPLOAD_URL)
                                .file(file)
                )
                .andExpect(status().isOk());

        verify(fileService)
                .saveDocument(any(DocumentToSave.class));
    }
}