package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import med.doctor_connect.model.FileTypeEnum;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileStoreDto {
    private String id;
    private String ownerUserId;
    private String relatedEntityType;
    private String relatedEntityId;
    private String fileKey;
    private String fileName;
    private FileTypeEnum fileType;
    private Long sizeBytes;
    private String checksum;
    private String uploadedById;
    private Date uploadedAt;
}
