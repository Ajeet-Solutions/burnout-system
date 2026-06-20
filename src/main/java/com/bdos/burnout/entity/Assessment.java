package com.bdos.burnout.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private int totalScore;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime executionDate;

    // Default Constructor
    public Assessment() {
    }

    // Parameterized Constructor
    public Assessment(String userEmail, int totalScore, String status, LocalDateTime executionDate) {
        this.userEmail = userEmail;
        this.totalScore = totalScore;
        this.status = status;
        this.executionDate = executionDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getExecutionDate() { return executionDate; }
    public void setExecutionDate(LocalDateTime executionDate) { this.executionDate = executionDate; }
}