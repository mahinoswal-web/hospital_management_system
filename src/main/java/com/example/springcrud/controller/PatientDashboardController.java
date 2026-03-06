package com.example.springcrud.controller;

import com.example.springcrud.model.Patient;
import com.example.springcrud.model.Prescription;
import com.example.springcrud.repository.PatientRepository;
import com.example.springcrud.repository.PrescriptionRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class PatientDashboardController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;

    @GetMapping("/patient/test-route")
    @ResponseBody
    public String testRoute() {
        return "SUCCESS! The Java routing is working perfectly!";
    }

    // ==========================================
    // 1. PATIENT MAIN DASHBOARD
    // ==========================================
    @GetMapping("/patient/patdashboard") 
    public String showPatientDashboard(Model model, HttpSession session) {
        String patientId = (String) session.getAttribute("loggedInPatientId");

        if (patientId == null) return "redirect:/login"; 

        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            model.addAttribute("patient", patient);
            return "patient/patdashboard"; 
        }

        return "redirect:/login";
    }

   // ==========================================
    // 2. PATIENT PRESCRIPTIONS PAGE
    // ==========================================
    @GetMapping("/patient/prescriptions") 
    public String viewPatientPrescriptions(Model model, HttpSession session) {
        
        String patientId = (String) session.getAttribute("loggedInPatientId");
        if (patientId == null) return "redirect:/login"; 

        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) return "redirect:/login";
        
        Patient patient = patientOpt.get();

        // Fetch ALL prescriptions sorted by the newest date first
        List<Prescription> allRx = prescriptionRepository.findByPatientIdOrderByDateDesc(patient.getPatientId());

        model.addAttribute("patient", patient);
        model.addAttribute("allPrescriptions", allRx); // Sending the unified list to the table!

        return "patient/patient-prescriptions";
    }
}