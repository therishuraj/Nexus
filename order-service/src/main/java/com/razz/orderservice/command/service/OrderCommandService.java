package com.razz.orderservice.command.service;

import com.razz.orderservice.dto.PlaceOrderRequest;
import com.razz.orderservice.model.write.Order;
import com.razz.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
public class OrderCommandService {
    private final OrderRepository repo;
    public OrderCommandService(OrderRepository repo) { this.repo = repo; }

    public Mono<String> place(PlaceOrderRequest cmd) {
        Order order = new Order(cmd.productId(), cmd.quantity(), cmd.funderId(), cmd.supplierId(), cmd.requestId());
        order.setUnitPrice(100.0); // Mock price
        order.setTotalAmount(order.getQuantity() * order.getUnitPrice());
        return repo.save(order).map(Order::getId);
    }

    public Mono<String> updateStatus(String id, String status) {
        return repo.findById(id)
                .doOnNext(o -> {
                    o.setStatus(status);
                    if ("DELIVERED".equals(status)) o.setDeliveredAt(LocalDateTime.now());
                })
                .flatMap(repo::save)
                .map(Order::getStatus);
    }

    public Mono<Void> pay(String id) {
        return repo.findById(id)
                .doOnNext(o -> {
                    o.setSupplierPaid(true);
                    o.setPaidAt(LocalDateTime.now());
                })
                .flatMap(repo::save)
                .then();
    }
}