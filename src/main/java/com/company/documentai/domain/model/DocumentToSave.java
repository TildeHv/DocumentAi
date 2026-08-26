package com.company.documentai.domain.model;

import lombok.NonNull;

import java.time.Instant;

public record DocumentToSave(
        @NonNull String fileName,
        @NonNull String fileType,
        @NonNull Instant createdAt,
        byte[] content) {
}
