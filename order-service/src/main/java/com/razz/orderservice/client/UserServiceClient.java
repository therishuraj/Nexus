package com.razz.orderservice.client;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.razz.orderservice.dto.UserUpdateRequest;

@Component
public class UserServiceClient {
    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    
    private final WebClient webClient;
    private final String userServiceUrl;

    public UserServiceClient(
            WebClient webClient,
            @Value("${user.service.url:http://localhost:3000}") String userServiceUrl) {
        this.webClient = webClient;
        this.userServiceUrl = userServiceUrl;
    }

    public void updateUserWallet(String userId, BigDecimal walletAdjustment) {
        String url = userServiceUrl + "/api/v1/users/" + userId;
        UserUpdateRequest request = new UserUpdateRequest(walletAdjustment);
        
        log.info("Calling user-service to update wallet - UserId: {}, Adjustment: {}", userId, walletAdjustment);
        
        try {
            Map<String, Object> response = webClient.put()
                    .uri(url)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response == null) {
                log.error("Received null response from user-service - UserId: {}", userId);
                throw new RuntimeException("No response from user-service");
            }
            
            if (Boolean.TRUE.equals(response.get("success"))) {
                log.info("User wallet updated successfully - UserId: {}, Response: {}", userId, response.get("message"));
            } else {
                String errorMessage = (String) response.get("error");
                if (errorMessage == null) {
                    errorMessage = "Unknown error";
                }
                log.error("Failed to update user wallet - UserId: {}, Error: {}", userId, errorMessage);
                throw new RuntimeException(errorMessage);
            }
            
        } catch (WebClientResponseException e) {
            log.error("Failed to update user wallet - UserId: {}, Status: {}, Error: {}", 
                    userId, e.getStatusCode(), e.getResponseBodyAsString());
            
            // Try to extract error message from response body
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage.contains("\"error\"")) {
                try {
                    // Extract error message from JSON response
                    int errorStart = errorMessage.indexOf("\"error\":\"") + 9;
                    int errorEnd = errorMessage.indexOf("\"", errorStart);
                    if (errorStart > 8 && errorEnd > errorStart) {
                        errorMessage = errorMessage.substring(errorStart, errorEnd);
                    }
                } catch (Exception ex) {
                    // Keep the full response if parsing fails
                }
            }
            throw new RuntimeException(errorMessage);
            
        } catch (Exception e) {
            log.error("Error calling user-service - UserId: {}, Error: {}", userId, e.getMessage());
            throw new RuntimeException("Error calling user-service: " + e.getMessage(), e);
        }
    }
}
