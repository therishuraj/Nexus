package com.razz.fundingms.repository;

import com.razz.fundingms.model.read.FundingRequestView;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FundingRequestViewRepository extends MongoRepository<FundingRequestView, String> {
    List<FundingRequestView> findByFunderName(String funderName);
    FundingRequestView findByRequestId(String requestId);
}
