package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "appointments")
public class DocAppointment {

    @Id
    private String id;

    private String appointmentId; // Custom ID like APT-001
    private String patientId;     // Reference to PAT-XXX
    private String doctorId;      // Reference to DOC-XXX
    
    private LocalDateTime appointmentDateTime; 
    private String reasonForVisit;             
    private String status;        // Set to "SCHEDULED" by default

    public DocAppointment() {
        this.status = "SCHEDULED";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getReasonForVisit() { return reasonForVisit; }
    public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}