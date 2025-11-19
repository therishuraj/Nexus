package com.razz.orderservice.query.controller;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.query.service.OrderQueryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
    private final OrderQueryService service;
    public OrderQueryController(OrderQueryService service) { this.service = service; }

    @GetMapping
    public List<OrderView> getByUser(@RequestParam String userId) { return service.getByUser(userId); }

    @GetMapping("/{id}")
    public OrderView getById(@PathVariable String id) { return service.getById(id); }

    @GetMapping("/views")
    public List<OrderView> getViews(@RequestParam String userId) { return service.getViews(userId); }
}