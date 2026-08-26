package com.company.documentai.infrastructure.database;

import com.company.documentai.domain.model.Document;
import com.company.documentai.domain.model.DocumentToSave;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "content", nullable = false)
    private byte[] content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static DocumentEntity fromDomain(@NonNull final DocumentToSave documentToSave) {
        return DocumentEntity.builder().fileName(documentToSave.fileName())
                .fileType(documentToSave.fileType())
                .content(documentToSave.content())
                .createdAt(documentToSave.createdAt())
                .build();
    }

    public Document toDomain() {
        return new Document(
                id,
                fileName,
                fileType,
                createdAt,
                content
        );
    }


}
