package com.razz.fundingms.command.controller;
import com.razz.fundingms.command.dto.CreateRequest;
import com.razz.fundingms.command.dto.FundingResponse;
import com.razz.fundingms.command.service.FundingCommandService;
import com.razz.fundingms.model.write.FundingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/funding/requests")
public class FundingCommandController {
    private final FundingCommandService service;
    public FundingCommandController(FundingCommandService service) { this.service = service; }

    @PostMapping
    public Mono<ResponseEntity<FundingResponse>> create(@RequestBody CreateRequest cmd) {
        return service.create(cmd)
                .map(id -> ResponseEntity.ok(new FundingResponse(id, "PENDING")));
    }
}