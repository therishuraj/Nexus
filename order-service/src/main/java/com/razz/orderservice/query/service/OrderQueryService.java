package com.razz.orderservice.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.repository.OrderViewRepository;

@Service
public class OrderQueryService {
    private final OrderViewRepository repo;
    public OrderQueryService(OrderViewRepository repo) { this.repo = repo; }

    public List<OrderView> getByUser(String userId) { return repo.findByFunderNameOrSupplierName(userId, userId); }
    public OrderView getById(String id) { return repo.findByOrderId(id); }
}