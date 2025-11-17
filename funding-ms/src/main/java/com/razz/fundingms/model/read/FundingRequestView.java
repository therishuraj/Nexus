package com.razz.fundingms.model.read;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "funding_request_views")
public class FundingRequestView {
    @Id private String requestId;
    private String title;
    private double currentFunded;
    private double requiredAmount;
    private String funderName;
    private List<InvestorShare> investors = new ArrayList<>();

    public static class InvestorShare {
        private String name;
        private double amount;
        private double sharePercent;
        public InvestorShare(String name, double amount, double sharePercent) {
            this.name = name; this.amount = amount; this.sharePercent = sharePercent;
        }
        public String getName() { return name; }
        public double getAmount() { return amount; }
        public double getSharePercent() { return sharePercent; }
    }

    // GETTERS & SETTERS
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getCurrentFunded() { return currentFunded; }
    public void setCurrentFunded(double currentFunded) { this.currentFunded = currentFunded; }
    public double getRequiredAmount() { return requiredAmount; }
    public void setRequiredAmount(double requiredAmount) { this.requiredAmount = requiredAmount; }
    public String getFunderName() { return funderName; }
    public void setFunderName(String funderName) { this.funderName = funderName; }
    public List<InvestorShare> getInvestors() { return investors; }
    public void setInvestors(List<InvestorShare> investors) { this.investors = investors; }
}