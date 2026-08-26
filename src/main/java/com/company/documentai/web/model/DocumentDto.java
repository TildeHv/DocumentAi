package com.company.documentai.web.model;

import com.company.documentai.domain.model.Document;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
        UUID id,
        String fileName,
        String fileType,
        Instant createdAt
) {

    public static DocumentDto fromDomain(@NonNull final Document document) {
        return new DocumentDto(
                document.id(),
                document.fileName(),
                document.fileType(),
                document.createdAt()
        );
    }
}
