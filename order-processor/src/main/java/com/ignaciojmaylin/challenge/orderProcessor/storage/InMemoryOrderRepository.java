package com.ignaciojmaylin.challenge.orderProcessor.storage;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryOrderRepository {
    private final Map<String, OrderRequest> store = new ConcurrentHashMap<>();

    public void save(OrderRequest request) {
        log.debug("Saving order: {}", request.orderId());
        store.put(request.orderId(), request);
    }

    public OrderRequest findById(String orderId) {
        log.debug("Fetching order: {}", orderId);
        return store.get(orderId);
    }
}