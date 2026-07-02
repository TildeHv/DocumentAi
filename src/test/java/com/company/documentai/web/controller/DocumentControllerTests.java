package com.company.documentai.web.controller;

import com.company.documentai.DocumentaiApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = DocumentaiApplication.class)
class DocumentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String UPLOAD_URL = "/api/v1/documents/upload";

    @Test
    @DisplayName("Ska lyckas ladda upp en textfil")
    void shouldUploadTextFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart(UPLOAD_URL).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.contentType").value(MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    @DisplayName("Ska lyckas ladda upp en PDF-fil")
    void shouldUploadPdfFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "%PDF-1.4...".getBytes()
        );

        mockMvc.perform(multipart(UPLOAD_URL).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("document.pdf"))
                .andExpect(jsonPath("$.contentType").value(MediaType.APPLICATION_PDF_VALUE));
    }

    @Test
    @DisplayName("Ska ge Bad Request om man laddar upp en JSON-fil")
    void shouldReturnBadRequestWhenJsonFileUploaded() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                "{}".getBytes()
        );

        mockMvc.perform(multipart(UPLOAD_URL).file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ska ge Bad Request om fil-parametern saknas")
    void shouldReturnBadRequestWhenFileParameterIsMissing() throws Exception {
        mockMvc.perform(multipart(UPLOAD_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ska hantera filer utan namn eller typ genom att svara 'unknown'")
    void shouldHandleMissingMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                null,
                "content".getBytes()
        );

        mockMvc.perform(multipart(UPLOAD_URL).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("unknown"))
                .andExpect(jsonPath("$.contentType").value("unknown"));
    }
}
