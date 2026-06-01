package com.burnout.service;

import com.burnout.model.Assessment;
import com.burnout.model.CopingStrategy; 
import com.burnout.repository.AssessmentRepository;
import com.burnout.repository.CopingStrategyRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List; 

@Service
public class AssessmentService {
    
    @Autowired
    private AssessmentRepository assessmentRepo;

    @Autowired
    private CopingStrategyRepository copingStrategyRepo; 

    // Process and persist assessment inputs into the database
    public Assessment saveAssessment(Assessment assessment) {
        // Check the dynamic total score coming from the frontend
        int total = assessment.getTotalScore();
        
        // Fallback: If for some reason the totalSquare from the frontend is 0 or empty, then the backend will calculate
        if (total == 0) {
            total = assessment.getQ1Score() + assessment.getQ2Score() + assessment.getQ3Score();
            assessment.setTotalScore(total);
        }

        // Exact mapping framework so thresholds mismatch no
        if (total >= 5) {
            assessment.setResultStatus("High Risk");
        } else if (total >= 3) { 
            assessment.setResultStatus("Medium Risk");
        } else {
            assessment.setResultStatus("Low Risk");
        }

        // Persist records
        return assessmentRepo.save(assessment);
    }

    // Fetch the most recent assessment transaction record mapped by user email identifier
    public Assessment getLatestAssessment(String email) {
        return assessmentRepo.findFirstByUserEmailOrderByAssessmentDateDesc(email).orElse(null);
    }

    //Retrieve the cumulative count of historical assessments submitted by a specific user account ID
    public long getAssessmentCountByUser(int userId) {
        return assessmentRepo.countByUserId(userId);
    }

    //Fetch dynamic coping strategies based on text-matching risk query
    public List<CopingStrategy> getStrategiesByLevel(String level) {
        System.out.println("Service Layer: Fetching data from Repository for level -> " + level);
        return copingStrategyRepo.findByBurnoutLevel(level);
    }
}