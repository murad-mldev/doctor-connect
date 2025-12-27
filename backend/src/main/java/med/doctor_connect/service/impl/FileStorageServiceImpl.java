package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.FileStoreDto;
import med.doctor_connect.model.FileStore;
import med.doctor_connect.model.FileTypeEnum;
import med.doctor_connect.model.User;
import med.doctor_connect.repository.FileStoreRepository;
import med.doctor_connect.repository.UserRepository;
import med.doctor_connect.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStoreRepository fileStoreRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public FileStoreDto uploadFile(MultipartFile file, UUID ownerUserId, String entityType, UUID entityId, FileTypeEnum fileType) {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        User owner = userRepository.findById(ownerUserId)
                .orElseThrow(() -> new RuntimeException("Owner user not found"));

        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique file key
            String originalFilename = file.getOriginalFilename();
            String fileKey = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileKey);

            // Copy file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save metadata
            FileStore fileStore = FileStore.builder()
                    .ownerUser(owner)
                    .relatedEntityType(entityType)
                    .relatedEntityId(entityId)
                    .fileKey(fileKey)
                    .fileName(originalFilename)
                    .fileType(fileType)
                    .sizeBytes(file.getSize())
                    .uploadedBy(owner)
                    .build();

            FileStore saved = fileStoreRepository.save(fileStore);

            return buildFileStoreDto(saved);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(UUID fileId) {
        FileStore fileStore = fileStoreRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            Path filePath = Paths.get(uploadDir).resolve(fileStore.getFileKey());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileStoreDto getFileById(UUID fileId) {
        FileStore fileStore = fileStoreRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        return buildFileStoreDto(fileStore);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FileStoreDto> getFilesByOwner(UUID ownerUserId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<FileStore> files = fileStoreRepository.findByOwnerUserId(ownerUserId, pageable);
        return files.map(this::buildFileStoreDto);
    }

    @Override
    public void deleteFile(UUID fileId) {
        FileStore fileStore = fileStoreRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            Path filePath = Paths.get(uploadDir).resolve(fileStore.getFileKey());
            Files.deleteIfExists(filePath);
            fileStoreRepository.delete(fileStore);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    private FileStoreDto buildFileStoreDto(FileStore fileStore) {
        return FileStoreDto.builder()
                .id(fileStore.getId().toString())
                .ownerUserId(fileStore.getOwnerUser() != null ? fileStore.getOwnerUser().getId().toString() : null)
                .relatedEntityType(fileStore.getRelatedEntityType())
                .relatedEntityId(fileStore.getRelatedEntityId() != null ? fileStore.getRelatedEntityId().toString() : null)
                .fileKey(fileStore.getFileKey())
                .fileName(fileStore.getFileName())
                .fileType(fileStore.getFileType())
                .sizeBytes(fileStore.getSizeBytes())
                .uploadedById(fileStore.getUploadedBy() != null ? fileStore.getUploadedBy().getId().toString() : null)
                .uploadedAt(fileStore.getUploadedAt())
                .build();
    }
}
