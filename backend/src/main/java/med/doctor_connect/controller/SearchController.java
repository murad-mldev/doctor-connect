package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "doctor") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, Object> response = new HashMap<>();

        if ("doctor".equalsIgnoreCase(type)) {
            Page<DoctorProfileDto> doctors = searchService.searchDoctors(q, page, limit);

            response.put("data", doctors.getContent());

            Map<String, Object> meta = new HashMap<>();
            meta.put("page", doctors.getNumber());
            meta.put("limit", doctors.getSize());
            meta.put("totalElements", doctors.getTotalElements());
            meta.put("totalPages", doctors.getTotalPages());
            response.put("meta", meta);
        } else {
            throw new RuntimeException("Unsupported search type: " + type);
        }

        return ResponseEntity.ok(response);
    }
}
