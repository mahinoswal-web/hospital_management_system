package com.example.springcrud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.TestResult;
import com.example.springcrud.repository.TestResultRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/results")
public class TestResultController {

    @Autowired
    private TestResultRepository testResultRepository;

    // Create a new test result (Usually starts as "Pending")
    @PostMapping
    public ResponseEntity<TestResult> createTestResult(@RequestBody TestResult testResult) {
        TestResult saved = testResultRepository.save(testResult);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Get all results for a specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TestResult>> getResultsByPatient(@PathVariable String patientId) {
        List<TestResult> results = testResultRepository.findByPatientId(patientId);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    // Update a test result (e.g., changing status to "Completed" and adding findings)
    @PutMapping("/{id}")
    public ResponseEntity<TestResult> updateTestResult(@PathVariable String id, @RequestBody TestResult updatedInfo) {
        Optional<TestResult> existingOpt = testResultRepository.findById(id);
        
        if (existingOpt.isPresent()) {
            TestResult existing = existingOpt.get();
            
            // Update the fields
            existing.setStatus(updatedInfo.getStatus());
            existing.setResultDetails(updatedInfo.getResultDetails());
            
            // If the status just changed to Completed, automatically set the date
            if ("Completed".equalsIgnoreCase(updatedInfo.getStatus()) && existing.getCompletedDate() == null) {
                existing.setCompletedDate(LocalDate.now());
            }
            
            TestResult saved = testResultRepository.save(existing);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}