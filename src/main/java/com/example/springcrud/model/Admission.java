package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "admissions")
public class Admission {

    @Id
    private String id;

    private String admissionId; 
    private String patientId;   
    private String doctorId;    
    
    private String reasonForAdmission; 
    private String wardOrRoom;         

    private LocalDateTime admissionDate; 
    private LocalDateTime dischargeDate; 
    private String status; 

    public Admission() {
        this.admissionDate = LocalDateTime.now();
        this.status = "ADMITTED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAdmissionId() { return admissionId; }
    public void setAdmissionId(String admissionId) { this.admissionId = admissionId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getReasonForAdmission() { return reasonForAdmission; }
    public void setReasonForAdmission(String reasonForAdmission) { this.reasonForAdmission = reasonForAdmission; }

    public String getWardOrRoom() { return wardOrRoom; }
    public void setWardOrRoom(String wardOrRoom) { this.wardOrRoom = wardOrRoom; }

    public LocalDateTime getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDateTime admissionDate) { this.admissionDate = admissionDate; }

    public LocalDateTime getDischargeDate() { return dischargeDate; }
    public void setDischargeDate(LocalDateTime dischargeDate) { this.dischargeDate = dischargeDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}