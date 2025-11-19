package com.razz.fundingms.repository;

import com.razz.fundingms.model.write.FundingRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FundingRequestRepository extends MongoRepository<FundingRequest, String> {

}