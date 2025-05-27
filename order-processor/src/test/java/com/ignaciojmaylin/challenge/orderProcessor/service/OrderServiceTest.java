package com.ignaciojmaylin.challenge.orderProcessor.service;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import com.ignaciojmaylin.challenge.orderProcessor.model.OrderResponse;
import com.ignaciojmaylin.challenge.orderProcessor.model.dao.OrderEntity;
import com.ignaciojmaylin.challenge.orderProcessor.model.enums.OrderStatus;
import com.ignaciojmaylin.challenge.orderProcessor.processor.OrderProcessor;
import com.ignaciojmaylin.challenge.orderProcessor.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderProcessor processor;

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest request;

    @BeforeEach
    void setup() {
        request = new OrderRequest("order123", "cust456", 100.0, new String[]{"item1"});
    }

    @Test
    void shouldReturnAlreadyExistsIfOrderExists() {
        OrderEntity existing = OrderEntity.builder()
                .orderId("order123")
                .status(OrderStatus.PENDING)
                .build();

        when(repository.findById("order123")).thenReturn(Optional.of(existing));

        OrderResponse response = orderService.initOrderAndReturnFast(request);

        assertEquals("AlreadyExists", response.status());
        assertEquals("order123", response.orderId());
        assertEquals(0, response.processingTimeMs());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnAcceptedAndSaveOrderIfNotExists() {
        when(repository.findById("order123")).thenReturn(Optional.empty());

        OrderResponse response = orderService.initOrderAndReturnFast(request);

        assertEquals("Accepted", response.status());
        assertEquals("order123", response.orderId());
        verify(repository).save(argThat(entity ->
                entity.getOrderId().equals("order123") &&
                        entity.getCustomerId().equals("cust456") &&
                        entity.getOrderAmount() == 100.0 &&
                        entity.getOrderItems().size() == 1 &&
                        entity.getStatus() == OrderStatus.PENDING
        ));
    }

    @Test
    void shouldReturnOrderFromRepository() {
        OrderEntity entity = OrderEntity.builder()
                .orderId("order123")
                .status(OrderStatus.APPROVED)
                .build();

        when(repository.findById("order123")).thenReturn(Optional.of(entity));

        Optional<OrderEntity> result = orderService.findOrderById("order123");

        assertTrue(result.isPresent());
        assertEquals("order123", result.get().getOrderId());
        assertEquals(OrderStatus.APPROVED, result.get().getStatus());
    }
}
