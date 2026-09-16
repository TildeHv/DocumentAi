package com.company.documentai.web.mapper;

import com.company.documentai.domain.model.DocumentToSave;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

public final class DocumentMapper {

    private DocumentMapper() {
    }

    public static DocumentToSave toDomain(
            final MultipartFile file,
            final String fileName
    ) throws IOException {

        final String fileType =
                StringUtils.hasText(file.getContentType())
                        ? file.getContentType()
                        : "application/octet-stream";

        return new DocumentToSave(
                fileName,
                fileType,
                Instant.now(),
                file.getBytes()
        );
    }
}