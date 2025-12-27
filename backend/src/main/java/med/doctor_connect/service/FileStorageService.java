package med.doctor_connect.service;

import med.doctor_connect.dto.FileStoreDto;
import med.doctor_connect.model.FileTypeEnum;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStorageService {

    FileStoreDto uploadFile(MultipartFile file, UUID ownerUserId, String entityType, UUID entityId, FileTypeEnum fileType);

    Resource downloadFile(UUID fileId);

    FileStoreDto getFileById(UUID fileId);

    Page<FileStoreDto> getFilesByOwner(UUID ownerUserId, int page, int limit);

    void deleteFile(UUID fileId);
}
