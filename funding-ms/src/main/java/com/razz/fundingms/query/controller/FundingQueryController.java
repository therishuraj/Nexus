package com.razz.fundingms.query.controller;

import com.razz.fundingms.model.read.FundingRequestView;
import com.razz.fundingms.query.service.FundingQueryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/funding/requests")
public class FundingQueryController {
    private final FundingQueryService service;
    public FundingQueryController(FundingQueryService service) { this.service = service; }

    @GetMapping("/views")
    public Flux<FundingRequestView> getViews(@RequestParam String funderId) {
        return service.getViews(funderId);
    }
}