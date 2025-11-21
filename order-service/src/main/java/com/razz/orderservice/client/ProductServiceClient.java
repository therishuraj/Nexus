package com.razz.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.razz.orderservice.dto.ProductResponse;

@Component
public class ProductServiceClient {
    private final WebClient webClient;
    private final String productServiceUrl;

    public ProductServiceClient(
            WebClient webClient,
            @Value("${product.service.url:http://localhost:8081}") String productServiceUrl) {
        this.webClient = webClient;
        this.productServiceUrl = productServiceUrl;
    }

    public ProductResponse getProductById(String productId) {
        String url = productServiceUrl + "/api/v1/product/" + productId;
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }

    public void updateProductQuantity(String productId, ProductResponse product, int newQuantity) {
        String url = productServiceUrl + "/api/v1/product/" + productId;
        
        // Create updated product with new quantity
        ProductResponse updatedProduct = new ProductResponse(
                product.id(),
                product.name(),
                product.category(),
                newQuantity,
                product.price(),
                product.supplierId()
        );
        
        webClient.put()
                .uri(url)
                .bodyValue(updatedProduct)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
