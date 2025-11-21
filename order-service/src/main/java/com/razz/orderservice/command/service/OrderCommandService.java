package com.razz.orderservice.command.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razz.orderservice.client.InvestmentServiceClient;
import com.razz.orderservice.client.ProductServiceClient;
import com.razz.orderservice.client.UserServiceClient;
import com.razz.orderservice.dto.FundingRequestResponse;
import com.razz.orderservice.dto.PlaceOrderRequest;
import com.razz.orderservice.dto.ProductResponse;
import com.razz.orderservice.model.read.OrderView;
import com.razz.orderservice.model.write.Order;
import com.razz.orderservice.repository.OrderRepository;
import com.razz.orderservice.repository.OrderViewRepository;

@Service
public class OrderCommandService {
    private static final Logger log = LoggerFactory.getLogger(OrderCommandService.class);
    
    private final OrderRepository repo;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final InvestmentServiceClient investmentServiceClient;
    private final OrderViewRepository orderViewRepository;
    
    @Value("${admin.user.id:admin_default_id}")
    private String adminUserId;
    
    public OrderCommandService(OrderRepository repo, ProductServiceClient productServiceClient, 
                                UserServiceClient userServiceClient, InvestmentServiceClient investmentServiceClient,
                                OrderViewRepository orderViewRepository) {
        this.repo = repo;
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
        this.investmentServiceClient = investmentServiceClient;
        this.orderViewRepository = orderViewRepository;
    }

    public String place(PlaceOrderRequest cmd) {
        log.info("Placing order - ProductId: {}, Quantity: {}, FunderId: {}, SupplierId: {}, RequestId: {}", 
                cmd.productId(), cmd.quantity(), cmd.funderId(), cmd.supplierId(), cmd.requestId());
        
        // Check funding request status from investment-service
        log.info("Checking funding request status from investment-service - RequestId: {}", cmd.requestId());
        FundingRequestResponse fundingRequest = investmentServiceClient.getFundingRequestById(cmd.requestId());
        
        if (fundingRequest == null) {
            log.error("Funding request not found - RequestId: {}", cmd.requestId());
            throw new RuntimeException("Funding request not found: " + cmd.requestId());
        }
        
        log.info("Funding request found - RequestId: {}, Status: {}, TargetAmount: {}, CurrentAmount: {}", 
                fundingRequest.id(), fundingRequest.status(), fundingRequest.targetAmount(), fundingRequest.currentAmount());
        
        // Validate funding request status is FUNDED
        if (!"FUNDED".equals(fundingRequest.status())) {
            log.error("Funding request is not fully funded - RequestId: {}, Status: {}", 
                    cmd.requestId(), fundingRequest.status());
            throw new RuntimeException("Funding request is not fully funded. Status: " + fundingRequest.status());
        }
        
        log.info("Funding request validation passed - RequestId: {} is FUNDED", cmd.requestId());
        
        // Get product details from product-service
        log.info("Fetching product details from product-service for productId: {}", cmd.productId());
        ProductResponse product = productServiceClient.getProductById(cmd.productId());
        
        if (product == null) {
            log.error("Product not found with productId: {}", cmd.productId());
            throw new RuntimeException("Product not found: " + cmd.productId());
        }
        
        log.info("Product found - Name: {}, Price: {}, Available Quantity: {}", 
                product.name(), product.price(), product.quantity());
        
        // Check if product has sufficient quantity
        if (product.quantity() < cmd.quantity()) {
            log.error("Insufficient product quantity for productId: {}. Available: {}, Requested: {}", 
                    cmd.productId(), product.quantity(), cmd.quantity());
            throw new RuntimeException("Insufficient product quantity. Available: " + product.quantity() + ", Requested: " + cmd.quantity());
        }
        
        log.info("Quantity validation passed. Creating order...");
        
        // Create order with price from product-service
        Order order = new Order(cmd.productId(), cmd.quantity(), cmd.funderId(), cmd.supplierId(), cmd.requestId());
        order.setUnitPrice(product.price());
        order.setTotalAmount(order.getQuantity() * order.getUnitPrice());
        
        log.info("Order created - UnitPrice: {}, TotalAmount: {}", 
                order.getUnitPrice(), order.getTotalAmount());
        
        // Update funder's wallet (deduct amount) before saving order
        BigDecimal walletAdjustment = BigDecimal.valueOf(-order.getTotalAmount());
        log.info("Updating funder wallet - FunderId: {}, Adjustment: {}", cmd.funderId(), walletAdjustment);
        
        try {
            userServiceClient.updateUserWallet(cmd.funderId(), walletAdjustment);
            log.info("Funder wallet updated successfully - FunderId: {}", cmd.funderId());
        } catch (RuntimeException e) {
            log.error("Failed to update funder wallet - FunderId: {}, Error: {}", cmd.funderId(), e.getMessage());
            throw new RuntimeException("Failed to update funder wallet: " + e.getMessage(), e);
        }
        
        log.info("Saving order to database");
        String orderId = repo.save(order).getId();
        
        log.info("Order placed successfully - OrderId: {}", orderId);
        
        // Sync to order_views (read model)
        syncToOrderView(order, product.name());
        
        // Update product quantity in product-service
        int newQuantity = product.quantity() - cmd.quantity();
        log.info("Updating product quantity in product-service - ProductId: {}, Old Quantity: {}, New Quantity: {}", 
                cmd.productId(), product.quantity(), newQuantity);
        
        productServiceClient.updateProductQuantity(cmd.productId(), product, newQuantity);
        
        log.info("Product quantity updated successfully");
        
        return orderId;
    }

    public String updateStatus(String id, String status) {
        log.info("Updating order status - OrderId: {}, NewStatus: {}", id, status);
        
        Optional<Order> opt = repo.findById(id);
        if (opt.isEmpty()) {
            log.error("Order not found - OrderId: {}", id);
            throw new RuntimeException("Order not found: " + id);
        }
        
        Order o = opt.get();
        o.setStatus(status);
        
        if ("DELIVERED".equals(status)) {
            log.info("Order marked as DELIVERED - OrderId: {}", id);
            o.setDeliveredAt(LocalDateTime.now());
            
            // Transfer amount from ADMIN wallet to Supplier wallet
            BigDecimal orderAmount = BigDecimal.valueOf(o.getTotalAmount());
            
            // Deduct from ADMIN wallet
            BigDecimal adminDeduction = orderAmount.negate(); // Negative for deduction
            log.info("Deducting from ADMIN wallet - AdminId: {}, Amount: {}", adminUserId, adminDeduction);
            
            try {
                userServiceClient.updateUserWallet(adminUserId, adminDeduction);
                log.info("ADMIN wallet updated successfully - AdminId: {}, Deducted: {}", adminUserId, orderAmount);
            } catch (RuntimeException e) {
                log.error("Failed to deduct from ADMIN wallet - AdminId: {}, Error: {}", adminUserId, e.getMessage());
                throw new RuntimeException("Failed to deduct from ADMIN wallet: " + e.getMessage(), e);
            }
            
            // Add to Supplier wallet
            log.info("Adding to Supplier wallet - SupplierId: {}, Amount: {}", o.getSupplierId(), orderAmount);
            
            try {
                userServiceClient.updateUserWallet(o.getSupplierId(), orderAmount);
                log.info("Supplier wallet updated successfully - SupplierId: {}, Added: {}", o.getSupplierId(), orderAmount);
            } catch (RuntimeException e) {
                log.error("Failed to add to Supplier wallet - SupplierId: {}, Error: {}", o.getSupplierId(), e.getMessage());
                
                // Rollback: Add amount back to ADMIN wallet
                try {
                    log.info("Rolling back ADMIN wallet - AdminId: {}", adminUserId);
                    userServiceClient.updateUserWallet(adminUserId, orderAmount);
                    log.info("ADMIN wallet rollback successful - AdminId: {}", adminUserId);
                } catch (RuntimeException rollbackException) {
                    log.error("Failed to rollback ADMIN wallet - AdminId: {}, Error: {}", 
                            adminUserId, rollbackException.getMessage());
                }
                
                throw new RuntimeException("Failed to add to Supplier wallet: " + e.getMessage(), e);
            }
        }
        
        repo.save(o);
        log.info("Order status updated successfully - OrderId: {}, Status: {}", id, status);
        
        // Sync to order_views (read model)
        syncOrderViewStatus(id, status);
        
        return o.getStatus();
    }
    
    private void syncToOrderView(Order order, String productName) {
        log.info("Syncing order to order_views - OrderId: {}", order.getId());
        
        OrderView view = new OrderView();
        view.setOrderId(order.getId());
        view.setProductName(productName);
        view.setQuantity(order.getQuantity());
        view.setTotalAmount(order.getTotalAmount());
        view.setFunderName(order.getFunderId()); // Using ID as name for now
        view.setSupplierName(order.getSupplierId()); // Using ID as name for now
        view.setStatus(order.getStatus());
        view.setSupplierPaid(order.isSupplierPaid());
        
        orderViewRepository.save(view);
        log.info("Order synced to order_views successfully - OrderId: {}", order.getId());
    }
    
    private void syncOrderViewStatus(String orderId, String status) {
        log.info("Updating order_views status - OrderId: {}, Status: {}", orderId, status);
        
        OrderView view = orderViewRepository.findByOrderId(orderId);
        if (view != null) {
            view.setStatus(status);
            orderViewRepository.save(view);
            log.info("Order_views status updated successfully - OrderId: {}", orderId);
        } else {
            log.warn("OrderView not found for update - OrderId: {}", orderId);
        }
    }
}