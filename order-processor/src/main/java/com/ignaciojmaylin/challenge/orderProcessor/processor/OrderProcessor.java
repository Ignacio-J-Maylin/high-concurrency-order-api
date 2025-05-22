package com.ignaciojmaylin.challenge.orderProcessor.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class OrderProcessor {

    public void simulateBusinessLogic() {
        int delay = ThreadLocalRandom.current().nextInt(100, 501);
        try {
            log.debug("Simulating business logic delay of {} ms", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Business logic simulation interrupted", e);
        }
    }
}