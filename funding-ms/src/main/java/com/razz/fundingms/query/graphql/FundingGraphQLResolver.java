package com.razz.fundingms.query.graphql;

import com.razz.fundingms.model.read.FundingRequestView;
import com.razz.fundingms.query.service.FundingQueryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Controller
public class FundingGraphQLResolver {

    private final FundingQueryService service;

    public FundingGraphQLResolver(FundingQueryService service) {
        this.service = service;
    }

    @QueryMapping
    public Mono<FundingRequestView> fundingRequestWithInvestors(@Argument String id) {
        return service.getById(id);
    }
}