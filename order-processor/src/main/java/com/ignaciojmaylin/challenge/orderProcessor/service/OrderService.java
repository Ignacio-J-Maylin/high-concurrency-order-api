package com.ignaciojmaylin.challenge.orderProcessor.service;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import com.ignaciojmaylin.challenge.orderProcessor.model.OrderResponse;
import com.ignaciojmaylin.challenge.orderProcessor.processor.OrderProcessor;
import com.ignaciojmaylin.challenge.orderProcessor.storage.InMemoryOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class OrderService {

    private final ExecutorService executor;
    private final OrderProcessor processor;
    private final InMemoryOrderRepository repository;

    public OrderService(ExecutorService executor, OrderProcessor processor, InMemoryOrderRepository repository) {
        this.executor = executor;
        this.processor = processor;
        this.repository = repository;
    }

    public CompletableFuture<OrderResponse> processOrder(OrderRequest request) {
        long startTime = System.currentTimeMillis();

        return CompletableFuture.supplyAsync(() -> {
            log.debug("Processing order: {}", request.orderId());
            processor.simulateBusinessLogic();
            repository.save(request);
            long duration = System.currentTimeMillis() - startTime;
            return new OrderResponse(request.orderId(), "Processed", duration);
        }, executor);
    }
}