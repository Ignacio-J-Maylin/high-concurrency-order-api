package com.ignaciojmaylin.challenge.orderProcessor.controller;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import com.ignaciojmaylin.challenge.orderProcessor.model.OrderResponse;
import com.ignaciojmaylin.challenge.orderProcessor.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
@Slf4j
@RestController
@RequestMapping("/processOrder")
class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<OrderResponse>> processOrder(@RequestBody OrderRequest request) {
        log.info("Received order: {}", request.orderId());

        if (request.orderId() == null || request.customerId() == null || request.orderItems() == null || request.orderItems().length == 0) {
            log.warn("Invalid order request: {}", request);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        }

        return orderService.processOrder(request)
                .thenApply(response -> {
                    log.info("Order {} processed in {} ms", response.orderId(), response.processingTimeMs());
                    return ResponseEntity.ok(response);
                });
    }
}