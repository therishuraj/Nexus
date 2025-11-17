package com.razz.orderservice.repository;

import com.razz.orderservice.model.read.OrderView;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderViewRepository extends ReactiveMongoRepository<OrderView, String> {
    @Query("{'$or': [{'funderName': ?0}, {'supplierName': ?0}]}")
    Flux<OrderView> findByFunderNameOrSupplierName(String funderName, String supplierName);

    Mono<OrderView> findByOrderId(String orderId);
}