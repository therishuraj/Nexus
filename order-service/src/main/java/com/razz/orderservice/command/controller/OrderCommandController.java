package com.razz.orderservice.command.controller;

import com.razz.orderservice.command.service.OrderCommandService;
import com.razz.orderservice.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}/pay")
    public ResponseEntity<PayResponse> pay(@PathVariable String id) {
        boolean paid = service.pay(id);
        return ResponseEntity.ok(new PayResponse(paid));
    }
}