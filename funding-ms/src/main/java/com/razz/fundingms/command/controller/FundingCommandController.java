package com.razz.fundingms.command.controller;

import com.razz.fundingms.command.dto.CreateRequest;
import com.razz.fundingms.command.dto.FundingResponse;
import com.razz.fundingms.command.service.FundingCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/funding/requests")
public class FundingCommandController {
    private final FundingCommandService service;

    public FundingCommandController(FundingCommandService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FundingResponse> create(@RequestBody CreateRequest cmd) {
        String id = service.create(cmd);
        return ResponseEntity.ok(new FundingResponse(id, "PENDING"));
    }
}