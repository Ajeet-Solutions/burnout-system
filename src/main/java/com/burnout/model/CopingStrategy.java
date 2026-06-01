package com.burnout.model;

import jakarta.persistence.*;

@Entity
@Table(name = "coping_strategies")
public class CopingStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "burnout_level", nullable = false)
    private String burnoutLevel; // Classification: Low Risk, Medium Risk, High Risk

    @Column(nullable = false)
    private String title; // Header/Title of the strategy (e.g., "5-Minute Meditation")

    @Column(columnDefinition = "TEXT")
    private String description; // Detailed action plan, clinical remedies, and coping tips

    @Column(name = "content_type")
    private String contentType; // Format type identifier Article Link, Video Link, or Text Content

    @Column(name = "resource_url")
    private String resourceUrl; // Direct reference web URL link for the video/article source

    // Default No-Argument Constructor for JPA Entity Lifecycle Management
    public CopingStrategy() {}

    // Encapsulated Getters and Setters Accessors
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBurnoutLevel() { return burnoutLevel; }
    public void setBurnoutLevel(String burnoutLevel) { this.burnoutLevel = burnoutLevel; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getResourceUrl() { return resourceUrl; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }
}