package com.razz.orderservice.query.service;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.repository.OrderViewRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderQueryService {
    private final OrderViewRepository repo;
    public OrderQueryService(OrderViewRepository repo) { this.repo = repo; }

    public List<OrderView> getByUser(String userId) { return repo.findByFunderNameOrSupplierName(userId, userId); }
    public OrderView getById(String id) { return repo.findByOrderId(id); }
    public List<OrderView> getViews(String userId) { return getByUser(userId); }
}