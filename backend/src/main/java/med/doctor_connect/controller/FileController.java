package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.FileStoreDto;
import med.doctor_connect.model.FileTypeEnum;
import med.doctor_connect.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/patients/{patientId}/reports")
    public ResponseEntity<FileStoreDto> uploadPatientReport(
            @PathVariable String patientId,
            @RequestParam("file") MultipartFile file) {

        UUID pid = UUID.fromString(patientId);
        FileStoreDto fileDto = fileStorageService.uploadFile(file, pid, "PATIENT_REPORT", pid, FileTypeEnum.MEDICAL_REPORT);
        return new ResponseEntity<>(fileDto, HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}/credentials")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<FileStoreDto> uploadDoctorCredentials(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {

        UUID uid = UUID.fromString(userId);
        FileStoreDto fileDto = fileStorageService.uploadFile(file, uid, "DOCTOR_CREDENTIAL", uid, FileTypeEnum.DOCTOR_CREDENTIAL);
        return new ResponseEntity<>(fileDto, HttpStatus.CREATED);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<Resource> downloadReport(@PathVariable String id) {
        UUID fileId = UUID.fromString(id);
        Resource file = fileStorageService.downloadFile(fileId);
        FileStoreDto fileDto = fileStorageService.getFileById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getFileName() + "\"")
                .body(file);
    }

    @GetMapping("/patients/{patientId}/reports")
    public ResponseEntity<Map<String, Object>> getPatientReports(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID pid = UUID.fromString(patientId);
        Page<FileStoreDto> files = fileStorageService.getFilesByOwner(pid, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", files.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", files.getNumber());
        meta.put("limit", files.getSize());
        meta.put("totalElements", files.getTotalElements());
        meta.put("totalPages", files.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }
}
