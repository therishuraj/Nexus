package com.razz.orderservice.query.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.query.service.OrderQueryService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
    private final OrderQueryService service;
    public OrderQueryController(OrderQueryService service) { this.service = service; }

    @GetMapping
    public List<OrderView> getByUser(@RequestParam String userId) { return service.getByUser(userId); }

    @GetMapping("/{id}")
    public OrderView getById(@PathVariable String id) { return service.getById(id); }
}