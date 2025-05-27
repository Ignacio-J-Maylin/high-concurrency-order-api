package com.ignaciojmaylin.challenge.orderProcessor.repository;

import com.ignaciojmaylin.challenge.orderProcessor.model.dao.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {}