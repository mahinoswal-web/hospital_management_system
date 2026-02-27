package com.example.springcrud.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.Medicines;
import com.example.springcrud.repository.MedicinesRepository;

@CrossOrigin(origins = "*") // Allow requests from frontend
@RestController
@RequestMapping("/api/medicines")
public class MedicinesController {

    @Autowired
    private MedicinesRepository medicinesRepository;

    // ==========================================
    // 1. CREATE MEDICINE
    // ==========================================
    @PostMapping("/create")
    public ResponseEntity<Medicines> createMedicine(@RequestBody Medicines medicine) {
        try {
            Medicines savedMedicine = medicinesRepository.save(medicine);
            return new ResponseEntity<>(savedMedicine, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==========================================
    // 2. VIEW ALL MEDICINES (With Optional Search Filters)
    // ==========================================
    @GetMapping("/all")
    public ResponseEntity<List<Medicines>> getAllMedicines(
            @RequestParam(required = false) String medId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String recordStatus,
            @RequestParam(required = false) String doctorId // <--- Added Doctor ID
    ) {
        try {
            List<Medicines> medicines = medicinesRepository.findAll();

            if (medicines.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Simple In-Memory Filtering 
            List<Medicines> filteredMedicines = medicines.stream()
                    .filter(m -> medId == null || (m.getMedId() != null && m.getMedId().toLowerCase().contains(medId.toLowerCase())))
                    .filter(m -> name == null || (m.getName() != null && m.getName().toLowerCase().contains(name.toLowerCase())))
                    .filter(m -> companyName == null || (m.getCompanyName() != null && m.getCompanyName().toLowerCase().contains(companyName.toLowerCase())))
                    .filter(m -> recordStatus == null || (m.getRecordStatus() != null && m.getRecordStatus().equalsIgnoreCase(recordStatus)))
                    .filter(m -> doctorId == null || (m.getDoctorId() != null && m.getDoctorId().equals(doctorId))) // <--- Filters by Doctor!
                    .collect(Collectors.toList());

            return new ResponseEntity<>(filteredMedicines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==========================================
    // 3. VIEW SINGLE MEDICINE BY DATABASE ID
    // ==========================================
    @GetMapping("/view/{id}")
    public ResponseEntity<Medicines> getMedicineById(@PathVariable("id") String id) {
        Optional<Medicines> medicine = medicinesRepository.findById(id);
        return medicine.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ==========================================
    // 4. UPDATE MEDICINE
    // ==========================================
    @PutMapping("/update/{id}")
    public ResponseEntity<Medicines> updateMedicine(@PathVariable("id") String id, @RequestBody Medicines medicine) {
        Optional<Medicines> medicineData = medicinesRepository.findById(id);

        if (medicineData.isPresent()) {
            Medicines _medicine = medicineData.get();
            _medicine.setMedId(medicine.getMedId());
            _medicine.setName(medicine.getName());
            _medicine.setCompanyName(medicine.getCompanyName());
            _medicine.setPrice(medicine.getPrice());
            _medicine.setRecordStatus(medicine.getRecordStatus());
            _medicine.setDosage(medicine.getDosage());
            _medicine.setRoute(medicine.getRoute());
            _medicine.setExpiryDate(medicine.getExpiryDate());
            _medicine.setDoctorChangeAllowed(medicine.getDoctorChangeAllowed());
            
            return new ResponseEntity<>(medicinesRepository.save(_medicine), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ==========================================
    // 5. DELETE MEDICINE
    // ==========================================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteMedicine(@PathVariable("id") String id) {
        try {
            medicinesRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}