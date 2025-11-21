package com.razz.orderservice.command.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razz.orderservice.command.service.OrderCommandService;
import com.razz.orderservice.dto.OrderResponse;
import com.razz.orderservice.dto.PlaceOrderRequest;
import com.razz.orderservice.dto.StatusRequest;
import com.razz.orderservice.dto.StatusResponse;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderCommandController {
    private final OrderCommandService service;
    public OrderCommandController(OrderCommandService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<OrderResponse> place(@RequestBody PlaceOrderRequest cmd) {
        String id = service.place(cmd);
        return ResponseEntity.ok(new OrderResponse(id, "PLACED"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<StatusResponse> updateStatus(@PathVariable String id, @RequestBody StatusRequest cmd) {
        String status = service.updateStatus(id, cmd.status());
        return ResponseEntity.ok(new StatusResponse(status));
    }
}