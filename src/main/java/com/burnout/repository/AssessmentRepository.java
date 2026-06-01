package com.burnout.repository;

import com.burnout.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {
    
    // Fetch all historical assessment records associated with a specific User ID
    List<Assessment> findByUserId(int userId);

    // Fetch the single most recent assessment record using the user's Email address
    @Query("SELECT a FROM Assessment a WHERE a.user.email = :email ORDER BY a.assessmentDate DESC LIMIT 1")
    Optional<Assessment> findFirstByUserEmailOrderByAssessmentDateDesc(@Param("email") String email);

 //Metrics Tracker: Automatically binds this method signature to a high-performance 'SELECT COUNT(*)' SQL database block.
    long countByUserId(int userId);
}