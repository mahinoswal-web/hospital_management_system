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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientDashboardController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;

    @GetMapping("/patdashboard") // You can also change the URL here if you want!
    public String showPatientDashboard(Model model, HttpSession session) {
        String patientId = (String) session.getAttribute("loggedInPatientId");

        if (patientId == null) {
            return "redirect:/login"; 
        }

        Optional<Patient> patientOpt = patientRepository.findById(patientId);

        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            model.addAttribute("patient", patient);

            List<Prescription> myPrescriptions = prescriptionRepository.findByPatientId(patient.getId());
            model.addAttribute("prescriptions", myPrescriptions);

            // *** UPDATED LINE HERE ***
            return "patient/patdashboard"; 
        }

        return "redirect:/login";
    }

    // 1. This defines your new URL
    @GetMapping("/patient/prescriptions")
    public String viewPatientPrescriptions(Model model, HttpSession session) {
        
        // 2. Check if the patient is logged in
        String patientId = (String) session.getAttribute("loggedInPatientId");
        if (patientId == null) {
            return "redirect:/api/auth/login"; 
        }

        // 3. Get the Patient's details
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            return "redirect:/api/auth/login";
        }
        Patient patient = patientOpt.get();

        // 4. Fetch all prescriptions for this patient
        List<Prescription> allRx = prescriptionRepository.findByPatientId(patientId);
        
        // Reverse them so the newest ones are at the top of the list
        java.util.Collections.reverse(allRx);

        Prescription latestRx = null;
        List<Prescription> pastRx = new ArrayList<>();

        // 5. Separate the latest prescription from the older ones
        if (allRx != null && !allRx.isEmpty()) {
            latestRx = allRx.get(0); // The very first one in the list (the newest)
            
            if (allRx.size() > 1) {
                pastRx = allRx.subList(1, allRx.size()); // The rest go to the history list
            }
        }

        // 6. Send this data to the HTML file
        model.addAttribute("patient", patient);
        model.addAttribute("latestRx", latestRx);
        model.addAttribute("pastRx", pastRx);

        // 7. This tells Spring Boot to load a file named "patient-prescriptions.html"
        return "patient-prescriptions"; 
    }
}