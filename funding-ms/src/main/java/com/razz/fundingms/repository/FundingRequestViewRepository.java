package com.razz.fundingms.repository;

import com.razz.fundingms.model.read.FundingRequestView;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FundingRequestViewRepository extends ReactiveMongoRepository<FundingRequestView, String> {
    Flux<FundingRequestView> findByFunderName(String funderName);
    Mono<FundingRequestView> findByRequestId(String requestId);
}
