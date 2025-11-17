package com.razz.orderservice.repository;

import com.razz.orderservice.model.write.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {}