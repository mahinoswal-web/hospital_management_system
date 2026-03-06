package com.example.springcrud.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.springcrud.model.TestResult;
import java.util.List;

@Repository
public interface TestResultRepository extends MongoRepository<TestResult, String> {
    
    // Find all tests for a specific patient (for the Patient Dashboard)
    List<TestResult> findByPatientId(String patientId);
    
    // Find all tests ordered by a specific doctor
    List<TestResult> findByDoctorId(String doctorId);
    
    // Find tests for a patient that are specifically "Pending" or "Completed"
    List<TestResult> findByPatientIdAndStatus(String patientId, String status);
}