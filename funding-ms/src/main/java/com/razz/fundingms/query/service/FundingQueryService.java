package com.razz.fundingms.query.service;

import org.springframework.stereotype.Service;

import com.razz.fundingms.model.read.FundingRequestView;
import com.razz.fundingms.repository.FundingRequestViewRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FundingQueryService {

    private final FundingRequestViewRepository viewRepository;

    // Constructor injection (no @Autowired needed in Spring Boot 3)
    public FundingQueryService(FundingRequestViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    // REST: GET /api/v1/funding/requests/views?funderId=...
    public Flux<FundingRequestView> getViews(String funderId) {
        return viewRepository.findByFunderName(funderId);
    }

    // REST: GET /api/v1/funding/requests/{id} + GraphQL
    public Mono<FundingRequestView> getById(String id) {
        return viewRepository.findByRequestId(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Funding request not found: " + id)));
    }

    // Optional: Get all (for admin or testing)
    public Flux<FundingRequestView> getAll() {
        return viewRepository.findAll();
    }
}