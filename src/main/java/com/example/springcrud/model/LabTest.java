package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lab_tests")
public class LabTest {

    @Id
    private String id;
    
    private String testId; // Your new custom ID (e.g., "TST-001")
    private String testName;
    private String description;
    
    // Constructors
    public LabTest() {}

    public LabTest(String testId, String testName, String description) {
        this.testId = testId;
        this.testName = testName;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // --- ADDED GETTER & SETTER FOR testId ---
    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}