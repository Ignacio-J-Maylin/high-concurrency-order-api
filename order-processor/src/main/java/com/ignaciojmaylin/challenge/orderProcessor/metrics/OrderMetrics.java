package com.ignaciojmaylin.challenge.orderProcessor.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
public class OrderMetrics {

    private final MeterRegistry registry;
    private final BlockingQueue<?> orderQueue;

    @Autowired
    public OrderMetrics(MeterRegistry registry, BlockingQueue<?> orderQueue) {
        this.registry = registry;
        this.orderQueue = orderQueue;
    }

    @PostConstruct
    public void init() {
        Gauge.builder("order.queue.size", orderQueue, BlockingQueue::size)
                .description("Current order queue size")
                .register(registry);
    }
}
