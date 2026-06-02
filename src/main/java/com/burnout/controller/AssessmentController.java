package com.burnout.controller;

import com.burnout.model.Assessment;
import com.burnout.model.Question; 
import com.burnout.model.User;
import com.burnout.model.CopingStrategy; 
import com.burnout.repository.UserRepository;
import com.burnout.repository.QuestionRepository; 
import com.burnout.service.AssessmentService;
import com.burnout.service.EmailService; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 🔥 FIXED: Spring Boot 3.x ke liye standard packages ensure kiye gye hain
import jakarta.servlet.http.HttpServletRequest; 
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List; 
import java.util.Optional;

@RestController
@RequestMapping("/api/assessment")
@CrossOrigin(origins = "*") 
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private QuestionRepository questionRepo; 

    @Autowired
    private EmailService emailService; 

    // Fetch and return the complete list of assessment questions
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionRepo.findAll();
        return ResponseEntity.ok(questions);
    }

    // Submit assessment data, persist records, and trigger automated rich email reports
    @PostMapping("/submit")
    public ResponseEntity<String> submitTest(@RequestParam("email") String email, @RequestBody Assessment assessment) {
        
        System.out.println("Incoming submission request for email: " + email);
        
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Error: User profile not found! Please log in first.");
        }

        User user = userOptional.get();
        assessment.setUser(user);
        
        // Service layer computes total score and saves record safely
        Assessment savedAssessment = assessmentService.saveAssessment(assessment);

        System.out.println("Triggering Rich Email Engine for: " + user.getEmail());

        try {
            // Sahi types direct pass ho rahe hain bina kisi mismatch ke
            emailService.sendBurnoutReport(
                user.getEmail(), 
                user.getName(), 
                user.getProfession(), 
                user.getAge(),              
                savedAssessment.getResultStatus(), 
                savedAssessment.getTotalScore()    
            );
            System.out.println("Rich HTML Email successfully pushed to mail server queue for: " + user.getEmail());
        } catch (Exception e) {
            System.out.println("Email processing failed but data saved: " + e.getMessage());
        }

        return ResponseEntity.ok("Success: Your assessment has been saved successfully.");
    }

    // Fetch the most recent assessment score for the result rendering dashboard
    @GetMapping("/latest")
    public ResponseEntity<Assessment> getLatestAssessment(@RequestParam String email) {
        Assessment latest = assessmentService.getLatestAssessment(email);
        if (latest != null) {
            return ResponseEntity.ok(latest);
        }
        return ResponseEntity.notFound().build();
    }

    // Retrieve the aggregated total count of assessments taken by a specific user profile
 // Retrieve the aggregated total count of assessments taken by a specific user profile
    @GetMapping("/count")
    public ResponseEntity<Long> getAssessmentCount(@RequestParam String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.ok(0L);
        }
        
        User user = userOptional.get();
        if (user.getId() == null) {
            return ResponseEntity.ok(0L);
        }

        // 🔥 FIX: Agar aapki service 'int' maangti hai, toh .intValue() pass karo
        // 🔥 Agar 'long' (primitive) maangti hai, toh .longValue() pass karo
        // Hum yahan primitive value nikaal kar bhej rahe hain taaki mismatch khatam ho jaye
        long count = assessmentService.getAssessmentCountByUser(user.getId().intValue());
        
        return ResponseEntity.ok(count);
    }

    // Dynamic Fetch handler from coping_strategies table
    @GetMapping("/strategies")
    public ResponseEntity<List<CopingStrategy>> getStrategiesByLevel(@RequestParam String level) {
        if (level == null || level.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String decodedLevel = URLDecoder.decode(level, StandardCharsets.UTF_8).trim();
        
        if (decodedLevel.toLowerCase().contains("high")) {
            decodedLevel = "High Risk";
        } else if (decodedLevel.toLowerCase().contains("medium")) {
            decodedLevel = "Medium Risk";
        } else if (decodedLevel.toLowerCase().contains("low")) {
            decodedLevel = "Low Risk";
        }

        System.out.println("Processing Database Query for Level: " + decodedLevel);
        List<CopingStrategy> strategies = assessmentService.getStrategiesByLevel(decodedLevel);
        
        if (strategies != null && !strategies.isEmpty()) {
            return ResponseEntity.ok(strategies);
        }
        return ResponseEntity.noContent().build();
    }
}