package com.example.springcrud.controller;

import com.example.springcrud.model.DoctorMedicine;
import com.example.springcrud.repository.DoctorMedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctor-inventory")
public class DoctorMedicineController {

    @Autowired 
    private DoctorMedicineRepository doctorMedicineRepository;

    @PostMapping("/add")
    public DoctorMedicine addMedicineToDocList(@RequestBody DoctorMedicine docMed) {
        return doctorMedicineRepository.save(docMed);
    }

    @GetMapping("/list/{doctorId}")
    public List<DoctorMedicine> getDocMedicines(@PathVariable String doctorId) {
        return doctorMedicineRepository.findByDoctorId(doctorId);
    }
}