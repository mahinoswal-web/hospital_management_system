package com.example.springcrud.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.MedicalTest;
import com.example.springcrud.repository.MedicalTestRepository;

@RestController
@RequestMapping("/api/medical_tests")
public class MedicalTestController {

    @Autowired
    private MedicalTestRepository medicinesTestRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<MedicalTest> createMedicinesTest(
            @RequestBody MedicalTest medicinesTest) {

        MedicalTest savedTest = medicinesTestRepository.save(medicinesTest);
        return new ResponseEntity<>(savedTest, HttpStatus.CREATED);
    }

    // READ - Get all tests (with filters)
    @GetMapping
    public ResponseEntity<List<MedicalTest>> getAllMedicinesTests(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String testName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String resultStatus,
            @RequestParam(required = false) Double maxPrice
    ) {

        List<MedicalTest> tests = medicinesTestRepository.findAll();

        List<MedicalTest> filteredTests = tests.stream()
                .filter(t -> patientId == null ||
                        (t.getPatientId() != null &&
                         patientId.equalsIgnoreCase(t.getPatientId())))
                .filter(t -> doctorId == null ||
                        (t.getDoctorId() != null &&
                         doctorId.equalsIgnoreCase(t.getDoctorId())))
                .filter(t -> testName == null ||
                        (t.getTestName() != null &&
                         t.getTestName().toLowerCase().contains(testName.toLowerCase())))
                .filter(t -> category == null ||
                        (t.getCategory() != null &&
                         category.equalsIgnoreCase(t.getCategory())))
                .filter(t -> resultStatus == null ||
                        (t.getResultStatus() != null &&
                         resultStatus.equalsIgnoreCase(t.getResultStatus())))
                .filter(t -> maxPrice == null ||
                        (t.getPrice() != null && t.getPrice() <= maxPrice))
                .collect(Collectors.toList());

        return new ResponseEntity<>(filteredTests, HttpStatus.OK);
    }

    // READ - Get test by Mongo _id
    @GetMapping("/{id}")
    public ResponseEntity<MedicalTest> getMedicinesTestById(@PathVariable String id) {
        Optional<MedicalTest> test = medicinesTestRepository.findById(id);
        return test.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // UPDATE (DetailsController style)
    @PutMapping("/{id}")
    public ResponseEntity<MedicalTest> updateMedicinesTest(
            @PathVariable String id,
            @RequestBody MedicalTest medicinesTest
    ) {

        Optional<MedicalTest> testOptional =
                medicinesTestRepository.findById(id);

        if (testOptional.isPresent()) {
            MedicalTest testToUpdate = testOptional.get();

            testToUpdate.setPatientId(medicinesTest.getPatientId());
            testToUpdate.setDoctorId(medicinesTest.getDoctorId());
            testToUpdate.setTestName(medicinesTest.getTestName());
            testToUpdate.setCategory(medicinesTest.getCategory());
            testToUpdate.setPrice(medicinesTest.getPrice());
            testToUpdate.setDescription(medicinesTest.getDescription());
            testToUpdate.setResultStatus(medicinesTest.getResultStatus());
            testToUpdate.setHistory(medicinesTest.getHistory());

            MedicalTest updatedTest =
                    medicinesTestRepository.save(testToUpdate);

            return new ResponseEntity<>(updatedTest, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteMedicinesTest(@PathVariable String id) {
        try {
            medicinesTestRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
