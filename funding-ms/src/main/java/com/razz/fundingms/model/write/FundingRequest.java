package com.razz.fundingms.model.write;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "funding_requests")
public class FundingRequest {
    @Id private String id;
    private String title;
    private double requiredAmount;
    private double currentFunded = 0.0;
    private String funderId;
    private String status = "PENDING";
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime deadline;

    public FundingRequest() {}

    public FundingRequest(String title, double requiredAmount, String funderId, String deadline) {
        this.title = title;
        this.requiredAmount = requiredAmount;
        this.funderId = funderId;
        this.deadline = LocalDateTime.parse(deadline);
    }

    // GETTERS & SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getRequiredAmount() { return requiredAmount; }
    public double getCurrentFunded() { return currentFunded; }
    public void setCurrentFunded(double currentFunded) { this.currentFunded = currentFunded; }
    public String getFunderId() { return funderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDeadline() { return deadline; }
}