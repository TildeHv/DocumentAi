package com.company.documentai.domain.model;

import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public record Document(
        @NonNull UUID id,
        @NonNull String fileName,
        @NonNull String fileType,
        @NonNull Instant createdAt,
        byte[] content
) {
}
