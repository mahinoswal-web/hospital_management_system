package com.example.springcrud.repository;

import com.example.springcrud.model.DoctorMedicine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorMedicineRepository extends MongoRepository<DoctorMedicine, String> {
    List<DoctorMedicine> findByDoctorId(String doctorId);
    
    // ADD THIS LINE: Finds the highest MED-XXX number in the database
    DoctorMedicine findTopByOrderByMedicineIdDesc();
}