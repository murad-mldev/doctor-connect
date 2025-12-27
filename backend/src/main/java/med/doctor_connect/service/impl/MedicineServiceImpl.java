package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.MedicineDto;
import med.doctor_connect.model.Medicine;
import med.doctor_connect.repository.MedicineRepository;
import med.doctor_connect.service.MedicineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    public MedicineDto createMedicine(MedicineDto medicineDto) {
        Medicine medicine = Medicine.builder()
                .name(medicineDto.getName())
                .dosageForm(medicineDto.getDosageForm())
                .manufacturer(medicineDto.getManufacturer())
                .description(medicineDto.getDescription())
                .isActive(true)
                .build();

        Medicine saved = medicineRepository.save(medicine);
        return buildMedicineDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicineDto getMedicineById(UUID id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        return buildMedicineDto(medicine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineDto> getAllActiveMedicines() {
        return medicineRepository.findByIsActiveTrue()
                .stream()
                .map(this::buildMedicineDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicineDto> searchMedicines(String name, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Medicine> medicines = medicineRepository.findByNameContainingIgnoreCase(name, pageable);
        return medicines.map(this::buildMedicineDto);
    }

    @Override
    public MedicineDto updateMedicine(UUID id, MedicineDto medicineDto) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        if (medicineDto.getName() != null) {
            medicine.setName(medicineDto.getName());
        }
        if (medicineDto.getDosageForm() != null) {
            medicine.setDosageForm(medicineDto.getDosageForm());
        }
        if (medicineDto.getManufacturer() != null) {
            medicine.setManufacturer(medicineDto.getManufacturer());
        }
        if (medicineDto.getDescription() != null) {
            medicine.setDescription(medicineDto.getDescription());
        }

        Medicine updated = medicineRepository.save(medicine);
        return buildMedicineDto(updated);
    }

    @Override
    public void deleteMedicine(UUID id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        medicine.setActive(false);
        medicineRepository.save(medicine);
    }

    private MedicineDto buildMedicineDto(Medicine medicine) {
        return MedicineDto.builder()
                .id(medicine.getId().toString())
                .name(medicine.getName())
                .dosageForm(medicine.getDosageForm())
                .manufacturer(medicine.getManufacturer())
                .description(medicine.getDescription())
                .isActive(medicine.isActive())
                .createdAt(medicine.getCreatedAt())
                .build();
    }
}
