package com.ignaciojmaylin.challenge.orderProcessor.config;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class QueueConfig {
    @Bean
    public BlockingQueue<OrderRequest> orderQueue() {
        return new LinkedBlockingQueue<>();
    }
}
