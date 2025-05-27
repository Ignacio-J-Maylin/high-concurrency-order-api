package com.ignaciojmaylin.challenge.orderProcessor.service;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import com.ignaciojmaylin.challenge.orderProcessor.model.OrderResponse;
import com.ignaciojmaylin.challenge.orderProcessor.model.dao.OrderEntity;
import com.ignaciojmaylin.challenge.orderProcessor.model.enums.OrderStatus;
import com.ignaciojmaylin.challenge.orderProcessor.processor.OrderProcessor;
import com.ignaciojmaylin.challenge.orderProcessor.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OrderService {

    private final OrderProcessor processor;
    private final OrderRepository repository;


    public OrderService(OrderProcessor processor, OrderRepository repository) {
        this.processor = processor;
        this.repository = repository;
    }

    public OrderResponse initOrderAndReturnFast(OrderRequest request) {
        long start = System.currentTimeMillis();

        Optional<OrderEntity> existing = repository.findById(request.orderId());
        if (existing.isPresent()) {
            log.info("Order ID {} already exists.", request.orderId());
            return new OrderResponse(request.orderId(), "AlreadyExists", 0);
        }

        OrderEntity entity = OrderEntity.builder()
                .orderId(request.orderId())
                .customerId(request.customerId())
                .orderAmount(request.orderAmount())
                .orderItems(List.of(request.orderItems()))
                .status(OrderStatus.PENDING)
                .build();

        repository.save(entity);
        processOrderAsync(request);

        long duration = System.currentTimeMillis() - start;
        return new OrderResponse(request.orderId(), "Accepted", duration);
    }

    @Async("applicationTaskExecutor")
    public void processOrderAsync(OrderRequest request) {
        try {
            CompletableFuture<Void> validation = processor.validateOrder(request);
            CompletableFuture<Void> stockCheck = processor.checkStock(request);
            CompletableFuture<Void> priceCalc = processor.calculatePrice(request);
            CompletableFuture<Void> stockReserve = processor.reserveStock(request);

            CompletableFuture.allOf(validation, stockCheck, priceCalc, stockReserve).join();

            updateOrderStatus(request.orderId(), OrderStatus.APPROVED);
        } catch (Exception e) {
            log.error("Saga failed for order {}, setting REJECTED", request.orderId(), e);
            updateOrderStatus(request.orderId(), OrderStatus.REJECTED);
        }
    }


    private void updateOrderStatus(String orderId, OrderStatus newStatus) {
        repository.findById(orderId).ifPresent(order -> {
            order.setStatus(newStatus);
            repository.save(order);
        });
    }

    public Optional<OrderEntity> findOrderById(String orderId) {
        return repository.findById(orderId);
    }

}