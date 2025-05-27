package com.ignaciojmaylin.challenge.orderProcessor.model.dao;

import com.ignaciojmaylin.challenge.orderProcessor.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private String orderId;
    private String customerId;
    private double orderAmount;

    @ElementCollection
    private List<String> orderItems;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

}