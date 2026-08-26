package com.company.documentai.infrastructure.service;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import com.company.documentai.infrastructure.database.DocumentEntity;
import com.company.documentai.infrastructure.database.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileServiceInfrastructure {

    private final DocumentRepository repository;

    public Document saveFile(@NonNull final DocumentToSave documentToSave) {
        return repository.save(DocumentEntity.fromDomain(documentToSave))
                .toDomain();
    }

    public List<Document> getAllDocuments() {
        return repository.findAll()
                .stream()
                .map(DocumentEntity::toDomain)
                .toList();
    }

    public Document getDocumentById(@NonNull final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"))
                .toDomain();
    }

    public void deleteDocument(@NonNull final UUID id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Document not found");
        }

        repository.deleteById(id);
    }
}