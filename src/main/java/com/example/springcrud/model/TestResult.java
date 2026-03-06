package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Document(collection = "test_results")
public class TestResult {

    @Id
    private String id;

    private String patientId; // e.g., PAT-001
    private String doctorId;  // e.g., DOC-105
    private String testName;  // e.g., Complete Blood Count (CBC)

    private String status;    // e.g., "Pending", "Completed"
    private String resultDetails; // The actual medical findings (e.g., "Hemoglobin levels normal.")

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prescribedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate completedDate;

    // ---------- CONSTRUCTORS ----------
    public TestResult() {
        this.prescribedDate = LocalDate.now();
        this.status = "Pending"; // Default status when a doctor first orders it
    }

    // ---------- GETTERS & SETTERS ----------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResultDetails() { return resultDetails; }
    public void setResultDetails(String resultDetails) { this.resultDetails = resultDetails; }

    public LocalDate getPrescribedDate() { return prescribedDate; }
    public void setPrescribedDate(LocalDate prescribedDate) { this.prescribedDate = prescribedDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
}