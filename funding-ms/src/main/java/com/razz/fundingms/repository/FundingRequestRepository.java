package com.razz.fundingms.repository;

import com.razz.fundingms.model.write.FundingRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FundingRequestRepository extends ReactiveMongoRepository<FundingRequest, String> {}