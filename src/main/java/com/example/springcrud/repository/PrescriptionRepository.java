package com.example.springcrud.repository;

import com.example.springcrud.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    
    Optional<Prescription> findByPrescriptionId(String prescriptionId);
    List<Prescription> findByPatientId(String patientId);
    List<Prescription> findByDoctorId(String doctorId);
    
    // ---> THE MAGIC SORTING LINE YOU NEED TO ADD <---
    List<Prescription> findByPatientIdOrderByDateDesc(String patientId);

    List<Prescription> findByMedicineListContainingIgnoreCase(String medicineName);
    List<Prescription> findByMedicineIdsContaining(String medId);
}