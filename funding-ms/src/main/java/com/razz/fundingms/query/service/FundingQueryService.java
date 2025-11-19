package com.razz.fundingms.query.service;

import com.razz.fundingms.model.read.FundingRequestView;
import com.razz.fundingms.repository.FundingRequestViewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundingQueryService {

    private final FundingRequestViewRepository viewRepository;

    // Constructor injection (no @Autowired needed in Spring Boot 3)
    public FundingQueryService(FundingRequestViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    // REST: GET /api/v1/funding/requests/views?funderId=...
    public List<FundingRequestView> getViews(String funderId) {
        return viewRepository.findByFunderName(funderId);
    }

    // REST: GET /api/v1/funding/requests/{id} + GraphQL
    public FundingRequestView getById(String id) {
        FundingRequestView view = viewRepository.findByRequestId(id);
        if (view == null) throw new RuntimeException("Funding request not found: " + id);
        return view;
    }

    // Optional: Get all (for admin or testing)
    public List<FundingRequestView> getAll() {
        return viewRepository.findAll();
    }
}