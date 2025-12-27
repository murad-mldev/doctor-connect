package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.DepartmentDto;
import med.doctor_connect.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meta")
@RequiredArgsConstructor
public class MetaController {

    private final DepartmentService departmentService;

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDto>> getDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto created = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
