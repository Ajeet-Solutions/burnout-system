package com.burnout.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty; 

@Entity
@Table(name = "assessments")
public class Assessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

   
    @Column(name = "q1_score")
    @JsonProperty("q1score")
    private int q1Score;

    @Column(name = "q2_score")
    @JsonProperty("q2score")
    private int q2Score;

    @Column(name = "q3_score")
    @JsonProperty("q3score")
    private int q3Score;

    @Column(name = "total_score")
    @JsonProperty("totalScore")
    private int totalScore;

    @Column(name = "result_status")
    @JsonProperty("resultStatus")
    private String resultStatus;

    @Column(name = "assessment_date")
    private LocalDateTime assessmentDate = LocalDateTime.now();

    public Assessment() {}

    
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