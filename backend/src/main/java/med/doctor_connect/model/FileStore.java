package med.doctor_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "file_store", indexes = {
    @Index(name = "idx_file_store_owner", columnList = "owner_user_id"),
    @Index(name = "idx_file_store_entity", columnList = "related_entity_type, related_entity_id")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class FileStore {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @Column(name = "related_entity_type")
    private String relatedEntityType;

    @Column(name = "related_entity_id", columnDefinition = "uuid")
    private UUID relatedEntityId;

    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    @Builder.Default
    private FileTypeEnum fileType = FileTypeEnum.OTHER;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "checksum")
    private String checksum;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Date uploadedAt;
}
