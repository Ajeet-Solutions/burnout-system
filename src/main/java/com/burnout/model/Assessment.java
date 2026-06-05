package com.burnout.model;

import jakarta.persistence.Column; 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
public class Assessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //DATABASE MAPPING
    @Column(name = "q1_score")
    private int q1Score;

    @Column(name = "q2_score")
    private int q2Score;

    @Column(name = "q3_score")
    private int q3Score;

    @Column(name = "total_score")
    private int totalScore;

    @Column(name = "result_status")
    private String resultStatus;

    @Column(name = "assessment_date")
    private LocalDateTime assessmentDate = LocalDateTime.now();

    // Default Constructor for JPA reflection mechanisms
    public Assessment() {}

    // Encapsulation Accessors (Getters and Setters)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getQ1Score() { return q1Score; }
    public void setQ1Score(int q1Score) { this.q1Score = q1Score; }

    public int getQ2Score() { return q2Score; }
    public void setQ2Score(int q2Score) { this.q2Score = q2Score; }

    public int getQ3Score() { return q3Score; }
    public void setQ3Score(int q3Score) { this.q3Score = q3Score; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }

    public LocalDateTime getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDateTime assessmentDate) { this.assessmentDate = assessmentDate; }
}