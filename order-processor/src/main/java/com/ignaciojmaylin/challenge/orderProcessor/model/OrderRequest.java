package com.ignaciojmaylin.challenge.orderProcessor.model;

public record OrderRequest(String orderId, String customerId, double orderAmount, String[] orderItems) {}
