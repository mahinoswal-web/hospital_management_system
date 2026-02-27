package com.example.springcrud.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springcrud.model.DocAppointment;
import com.example.springcrud.repository.DocAppointmentRepository;

@RestController
@RequestMapping("/api/appointments")
public class DocAppointmentController {

    @Autowired
    private DocAppointmentRepository appointmentRepository;

    // ==========================================
    // 1. DOCTOR-SIDE BOOKING (With Slot Check)
    // ==========================================
    @PostMapping("/book")
    public ResponseEntity<?> createAppointment(@RequestBody DocAppointment appointment) {
        
        // A. SLOT VALIDATION: Check if doctor is already busy at this exact time
        boolean isBusy = appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
            appointment.getDoctorId(), 
            appointment.getAppointmentDateTime()
        );

        if (isBusy) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Error: This doctor already has an appointment scheduled for " + appointment.getAppointmentDateTime());
        }

        // B. ID GENERATION: Auto-generate APT-XXX ID if not provided
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isEmpty()) {
            long count = appointmentRepository.count();
            appointment.setAppointmentId(String.format("APT-%03d", count + 1));
        }
        
        // C. STATUS: Default to SCHEDULED for doctor-side bookings
        appointment.setStatus("SCHEDULED");
        
        DocAppointment savedAppointment = appointmentRepository.save(appointment);
        return new ResponseEntity<>(savedAppointment, HttpStatus.CREATED);
    }

    // ==========================================
    // 2. GET APPOINTMENT BY ID
    // ==========================================
    @GetMapping("/id/{appointmentId}")
    public ResponseEntity<DocAppointment> getAppointmentById(@PathVariable String appointmentId) {
        Optional<DocAppointment> appointment = appointmentRepository.findFirstByAppointmentId(appointmentId);
        return appointment.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ==========================================
    // 3. GET DOCTOR'S SCHEDULE (Filtered by status)
    // ==========================================
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DocAppointment>> getDoctorAppointments(
            @PathVariable String doctorId,
            @RequestParam(required = false) String status) {
        
        List<DocAppointment> appointments;
        
        if (status != null && !status.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndStatusIgnoreCase(doctorId, status);
        } else {
            appointments = appointmentRepository.findByDoctorId(doctorId);
        }
        
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // ==========================================
    // 4. GET PATIENT'S APPOINTMENT HISTORY
    // ==========================================
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DocAppointment>> getPatientAppointments(@PathVariable String patientId) {
        List<DocAppointment> appointments = appointmentRepository.findByPatientId(patientId);
        
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // ==========================================
    // 5. UPDATE APPOINTMENT STATUS
    // ==========================================
    @PutMapping("/status/{appointmentId}")
    public ResponseEntity<DocAppointment> updateAppointmentStatus(
            @PathVariable String appointmentId,
            @RequestParam String status) {
            
        return appointmentRepository.findFirstByAppointmentId(appointmentId).map(appointment -> {
            appointment.setStatus(status.toUpperCase());
            DocAppointment updatedAppointment = appointmentRepository.save(appointment);
            return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ==========================================
    // 6. DELETE APPOINTMENT
    // ==========================================
    @DeleteMapping("/id/{appointmentId}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable String appointmentId) {
        try {
            Optional<DocAppointment> appointment = appointmentRepository.findFirstByAppointmentId(appointmentId);
            if (appointment.isPresent()) {
                appointmentRepository.delete(appointment.get());
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}