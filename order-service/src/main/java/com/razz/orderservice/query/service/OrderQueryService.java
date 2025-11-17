package com.razz.orderservice.query.service;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.repository.OrderViewRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderQueryService {
    private final OrderViewRepository repo;
    public OrderQueryService(OrderViewRepository repo) { this.repo = repo; }

    public Flux<OrderView> getByUser(String userId) { return repo.findByFunderNameOrSupplierName(userId, userId); }
    public Mono<OrderView> getById(String id) { return repo.findByOrderId(id); }
    public Flux<OrderView> getViews(String userId) { return getByUser(userId); }
}