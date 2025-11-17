package com.razz.orderservice.query.controller;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.query.service.OrderQueryService;
import org.springframework.web.bind.annotation.*;
        import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
    private final OrderQueryService service;
    public OrderQueryController(OrderQueryService service) { this.service = service; }

    @GetMapping
    public Flux<OrderView> getByUser(@RequestParam String userId) {
        return service.getByUser(userId);
    }

    @GetMapping("/{id}")
    public Mono<OrderView> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/views")
    public Flux<OrderView> getViews(@RequestParam String userId) {
        return service.getViews(userId);
    }
}