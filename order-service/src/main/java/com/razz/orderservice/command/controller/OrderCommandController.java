package com.razz.orderservice.command.controller;

import com.razz.orderservice.command.service.OrderCommandService;
import com.razz.orderservice.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderCommandController {
    private final OrderCommandService service;
    public OrderCommandController(OrderCommandService service) { this.service = service; }

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> place(@RequestBody PlaceOrderRequest cmd) {
        return service.place(cmd)
                .map(id -> ResponseEntity.ok(new OrderResponse(id, "PLACED")));
    }

    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<StatusResponse>> updateStatus(@PathVariable String id, @RequestBody StatusRequest cmd) {
        return service.updateStatus(id, cmd.status())
                .map(status -> ResponseEntity.ok(new StatusResponse(status)));
    }

    @PutMapping("/{id}/pay")
    public Mono<ResponseEntity<PayResponse>> pay(@PathVariable String id) {
        return service.pay(id)
                .then(Mono.just(ResponseEntity.ok(new PayResponse(true))));
    }
}