package com.example.springcrud.repository;
 
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.springcrud.model.LabTest;
import java.util.Optional;

@Repository
public interface LabTestRepository extends MongoRepository<LabTest, String> {
    
    // Custom search method to find a test by TST-XXX (This is what Maven is looking for!)
    Optional<LabTest> findByTestId(String testId);
}