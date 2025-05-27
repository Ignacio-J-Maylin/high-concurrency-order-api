package com.ignaciojmaylin.challenge.orderProcessor.processor;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class OrderProcessor {
    private void simulateDelay(String action, String orderId) {
        try {
            int delay = ThreadLocalRandom.current().nextInt(100, 501); // 100–500 ms
            log.debug("{} for order {} (delay: {} ms)", action, orderId, delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted during {} for order {}", action, orderId);
        }
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Void> validateOrder(OrderRequest request) {
        simulateDelay("Validating order", request.orderId());
        return CompletableFuture.completedFuture(null);
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Void> checkStock(OrderRequest request) {
        simulateDelay("Checking stock", request.orderId());
        return CompletableFuture.completedFuture(null);
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Void> calculatePrice(OrderRequest request) {
        simulateDelay("Calculating price", request.orderId());
        return CompletableFuture.completedFuture(null);
    }

    @Async("applicationTaskExecutor")
    public CompletableFuture<Void> reserveStock(OrderRequest request) {
        simulateDelay("Reserving stock", request.orderId());
        return CompletableFuture.completedFuture(null);
    }
}