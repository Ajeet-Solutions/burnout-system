package com.burnout.repository;

import com.burnout.model.CopingStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CopingStrategyRepository extends JpaRepository<CopingStrategy, Integer> {
    
    // Retrieves a list of coping strategies filtered by specific burnout risk levels (High, Medium, Low)
    List<CopingStrategy> findByBurnoutLevel(String burnoutLevel);
}