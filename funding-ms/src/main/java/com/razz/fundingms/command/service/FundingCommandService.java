package com.razz.fundingms.command.service;

import com.razz.fundingms.command.dto.CreateRequest;
import com.razz.fundingms.model.write.FundingRequest;
import com.razz.fundingms.repository.FundingRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class FundingCommandService {
    private final FundingRequestRepository repo;
    public FundingCommandService(FundingRequestRepository repo) { this.repo = repo; }
    public String create(CreateRequest cmd) { FundingRequest req = new FundingRequest(cmd.title(), cmd.requiredAmount(), cmd.funderId(), cmd.deadline()); FundingRequest saved = repo.save(req); return saved.getId(); }
}