package com.company.documentai.domain.service;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.infrastructure.service.DocumentTextExtractorImpl;
import com.company.documentai.infrastructure.service.FileServiceInfrastructure;
import com.company.documentai.infrastructure.service.OllamaClient;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileService {

    private final FileServiceInfrastructure infrastructure;
    private final DocumentTextExtractorImpl textExtractor;
    private final OllamaClient ollamaClient;

    public Document saveDocument(
            @NonNull final DocumentToSave documentToSave
    ) {
        return infrastructure.saveFile(documentToSave);
    }

    public List<Document> getAllDocuments() {
        return infrastructure.getAllDocuments();
    }

    public Document getDocumentById(
            @NonNull final UUID id
    ) {
        return infrastructure.getDocumentById(id);
    }

    public void deleteDocument(
            @NonNull final UUID id
    ) {
        infrastructure.deleteDocument(id);
    }

    public String summarizeDocument(
            @NonNull final UUID id
    ) {
        final Document document =
                infrastructure.getDocumentById(id);

        final String text =
                textExtractor.extract(
                        document.fileType(),
                        document.content()
                );

        return ollamaClient.summarize(text);
    }
}