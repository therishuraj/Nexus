package com.razz.orderservice.command.service;

import com.razz.orderservice.dto.PlaceOrderRequest;
import com.razz.orderservice.model.write.Order;
import com.razz.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderCommandService {
    private final OrderRepository repo;
    public OrderCommandService(OrderRepository repo) { this.repo = repo; }

    public String place(PlaceOrderRequest cmd) {
        Order order = new Order(cmd.productId(), cmd.quantity(), cmd.funderId(), cmd.supplierId(), cmd.requestId());
        order.setUnitPrice(100.0);
        order.setTotalAmount(order.getQuantity() * order.getUnitPrice());
        return repo.save(order).getId();
    }

    public String updateStatus(String id, String status) {
        Optional<Order> opt = repo.findById(id);
        if (opt.isEmpty()) throw new RuntimeException("Order not found: " + id);
        Order o = opt.get();
        o.setStatus(status);
        if ("DELIVERED".equals(status)) o.setDeliveredAt(LocalDateTime.now());
        repo.save(o);
        return o.getStatus();
    }

    public boolean pay(String id) {
        Optional<Order> opt = repo.findById(id);
        if (opt.isEmpty()) throw new RuntimeException("Order not found: " + id);
        Order o = opt.get();
        o.setSupplierPaid(true);
        o.setPaidAt(LocalDateTime.now());
        repo.save(o);
        return true;
    }
}