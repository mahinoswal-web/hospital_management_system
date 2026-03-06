package com.example.springcrud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.LabTest;
import com.example.springcrud.repository.LabTestRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lab-tests")
public class LabTestController {

    @Autowired
    private LabTestRepository labTestRepository;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<LabTest> createLabTest(@RequestBody LabTest labTest) {
        // Automatically generate TST-XXX if the user doesn't provide one
        if (labTest.getTestId() == null || labTest.getTestId().isEmpty()) {
            long count = labTestRepository.count();
            labTest.setTestId(String.format("TST-%03d", count + 1));
        }
        
        LabTest savedTest = labTestRepository.save(labTest);
        return new ResponseEntity<>(savedTest, HttpStatus.CREATED);
    }

    // ================= READ ALL =================
    @GetMapping
    public ResponseEntity<List<LabTest>> getAllLabTests() {
        List<LabTest> tests = labTestRepository.findAll();
        return new ResponseEntity<>(tests, HttpStatus.OK);
    }

    // ================= READ BY testId =================
    @GetMapping("/{testId}")
    public ResponseEntity<LabTest> getLabTestById(@PathVariable String testId) {
        Optional<LabTest> test = labTestRepository.findByTestId(testId);
        return test.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ================= UPDATE BY testId =================
    @PutMapping("/{testId}")
    public ResponseEntity<LabTest> updateLabTest(@PathVariable String testId, @RequestBody LabTest labTestDetails) {
        Optional<LabTest> testOptional = labTestRepository.findByTestId(testId);

        if (testOptional.isPresent()) {
            LabTest existingTest = testOptional.get();
            
            existingTest.setTestName(labTestDetails.getTestName());
            existingTest.setDescription(labTestDetails.getDescription());
            // We do not update the testId to keep it consistent

            LabTest updatedTest = labTestRepository.save(existingTest);
            return new ResponseEntity<>(updatedTest, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ================= DELETE BY testId =================
    @DeleteMapping("/{testId}")
    public ResponseEntity<HttpStatus> deleteLabTest(@PathVariable String testId) {
        try {
            Optional<LabTest> testOptional = labTestRepository.findByTestId(testId);
            if (testOptional.isPresent()) {
                labTestRepository.delete(testOptional.get());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}