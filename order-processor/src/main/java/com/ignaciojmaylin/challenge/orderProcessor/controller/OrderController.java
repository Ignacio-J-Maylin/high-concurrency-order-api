package com.ignaciojmaylin.challenge.orderProcessor.controller;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import com.ignaciojmaylin.challenge.orderProcessor.model.OrderResponse;
import com.ignaciojmaylin.challenge.orderProcessor.model.dao.OrderEntity;
import com.ignaciojmaylin.challenge.orderProcessor.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/processOrder")
class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> processOrder(@RequestBody OrderRequest request) {
        log.info("Received order: {}", request.orderId());

        if (request.orderId() == null || request.customerId() == null || request.orderItems() == null || request.orderItems().length == 0) {
            log.warn("Invalid order request: {}", request);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        OrderResponse response = orderService.initOrderAndReturnFast(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<String> getOrderStatus(@PathVariable String orderId) {
        Optional<OrderEntity> order = orderService.findOrderById(orderId);
        if (order.isPresent()) {
            return ResponseEntity.ok(order.get().getStatus().name());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
    }
}