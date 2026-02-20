package com.example.springcrud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.example.springcrud.model.LoginRequest;
import com.example.springcrud.model.Admin;
import com.example.springcrud.model.Doctor;
import com.example.springcrud.model.Patient;
import com.example.springcrud.repository.AdminRepository;
import com.example.springcrud.repository.DoctorRepository;
import com.example.springcrud.repository.PatientRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AdminRepository adminRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PatientRepository patientRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        String role = request.getRole();
        String phone = request.getPhone();
        String password = request.getPassword();

        Map<String, Object> response = new HashMap<>();

        try {
            // 1. ADMIN LOGIN
            if ("ADMIN".equalsIgnoreCase(role)) {
                Optional<Admin> adminOpt = adminRepository.findByPhone(phone);
                if (adminOpt.isPresent()) {
                    Admin admin = adminOpt.get();
                    if (password.equals(admin.getPasswordHash())) {
                        admin.setLastLoginAt(LocalDateTime.now());
                        adminRepository.save(admin);

                        // Save session for security
                        session.setAttribute("loggedInAdminId", admin.getId());

                        response.put("message", "Welcome Admin " + admin.getUsername());
                        response.put("redirect", "/admin/dashboard");
                        return ResponseEntity.ok(response);
                    }
                }
            }

            // 2. DOCTOR LOGIN
            else if ("DOCTOR".equalsIgnoreCase(role)) {
                Optional<Doctor> docOpt = doctorRepository.findByPhone(phone);
                if (docOpt.isPresent()) {
                    Doctor doc = docOpt.get();
                    if (password.equals(doc.getPassword())) {
                        session.setAttribute("loggedInDoctorId", doc.getId());
                        response.put("message", "Welcome Dr. " + doc.getName());
                        response.put("redirect", "/doctor/docdashboard");
                        return ResponseEntity.ok(response);
                    }
                }
            }

            // 3. PATIENT LOGIN
            else if ("PATIENT".equalsIgnoreCase(role)) {
                Optional<Patient> patOpt = patientRepository.findByPhone(phone);
                if (patOpt.isPresent()) {
                    Patient pat = patOpt.get();
                    if (password.equals(pat.getPassword())) {
                        session.setAttribute("loggedInPatientId", pat.getId());
                        response.put("message", "Welcome " + pat.getFullName());
                        
                        // *** UPDATED: Now redirects to the correct patdashboard URL ***
                        response.put("redirect", "/patient/patdashboard");
                        return ResponseEntity.ok(response);
                    }
                }
            }

            // *** UPDATED: Return errors as JSON so frontend JS (data.message) works correctly ***
            response.put("message", "Invalid Phone Number or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Server Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}