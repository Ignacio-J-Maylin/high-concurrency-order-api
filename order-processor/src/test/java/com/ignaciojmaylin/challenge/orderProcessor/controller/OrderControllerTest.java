package com.ignaciojmaylin.challenge.orderProcessor.controller;

import com.ignaciojmaylin.challenge.orderProcessor.model.OrderRequest;
import com.ignaciojmaylin.challenge.orderProcessor.model.OrderResponse;
import com.ignaciojmaylin.challenge.orderProcessor.model.dao.OrderEntity;
import com.ignaciojmaylin.challenge.orderProcessor.model.enums.OrderStatus;
import com.ignaciojmaylin.challenge.orderProcessor.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new OrderRequest("order123", "cust456", 100.0, new String[]{"item1"});
    }

    @Test
    void shouldReturnAcceptedForValidOrder() {
        OrderResponse response = new OrderResponse("order123", "Accepted", 123);
        when(orderService.initOrderAndReturnFast(validRequest)).thenReturn(response);

        ResponseEntity<OrderResponse> result = orderController.processOrder(validRequest);

        assertEquals(202, result.getStatusCodeValue());
        assertEquals("Accepted", result.getBody().status());
    }

    @Test
    void shouldReturnBadRequestForInvalidOrder() {
        OrderRequest invalidRequest = new OrderRequest(null, null, 0.0, null);

        ResponseEntity<OrderResponse> result = orderController.processOrder(invalidRequest);

        assertEquals(400, result.getStatusCodeValue());
        assertNull(result.getBody());
    }

    @Test
    void shouldReturnStatusWhenOrderExists() {
        OrderEntity entity = OrderEntity.builder()
                .orderId("order123")
                .status(OrderStatus.APPROVED)
                .build();

        when(orderService.findOrderById("order123")).thenReturn(Optional.of(entity));

        ResponseEntity<String> result = orderController.getOrderStatus("order123");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("APPROVED", result.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() {
        when(orderService.findOrderById("order123")).thenReturn(Optional.empty());

        ResponseEntity<String> result = orderController.getOrderStatus("order123");

        assertEquals(404, result.getStatusCodeValue());
        assertEquals("Order not found", result.getBody());
    }
}
