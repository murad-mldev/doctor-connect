package med.doctor_connect.service;

import med.doctor_connect.dto.MedicineDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface MedicineService {

    MedicineDto createMedicine(MedicineDto medicineDto);

    MedicineDto getMedicineById(UUID id);

    List<MedicineDto> getAllActiveMedicines();

    Page<MedicineDto> searchMedicines(String name, int page, int limit);

    MedicineDto updateMedicine(UUID id, MedicineDto medicineDto);

    void deleteMedicine(UUID id);
}
