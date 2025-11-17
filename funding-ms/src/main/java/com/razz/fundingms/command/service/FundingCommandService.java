package com.razz.fundingms.command.service;

import com.razz.fundingms.command.dto.CreateRequest;
//import com.razz.fundingms.command.controller.FundingCommandController.CreateRequest;
import com.razz.fundingms.model.write.FundingRequest;
import com.razz.fundingms.repository.FundingRequestRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FundingCommandService {
    private final FundingRequestRepository repo;
    public FundingCommandService(FundingRequestRepository repo) { this.repo = repo; }

    public Mono<String> create(CreateRequest cmd) {
        FundingRequest req = new FundingRequest(cmd.title(), cmd.requiredAmount(), cmd.funderId(), cmd.deadline());
        return repo.save(req).map(FundingRequest::getId);
    }
}