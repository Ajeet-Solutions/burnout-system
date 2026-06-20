package com.bdos.burnout.repository;

import com.bdos.burnout.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    // 1. Custom query to get history of a single specific user (Used in User Dashboard)
    List<Assessment> findByUserEmailOrderByExecutionDateDesc(String userEmail);

    // 2.Fetch ALL records from the database sorted by latest date (Used in Admin Dashboard)
    List<Assessment> findAllByOrderByExecutionDateDesc();
}