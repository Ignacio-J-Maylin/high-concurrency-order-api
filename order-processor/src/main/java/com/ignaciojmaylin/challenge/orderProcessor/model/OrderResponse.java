package com.ignaciojmaylin.challenge.orderProcessor.model;

public record OrderResponse(String orderId, String status, long processingTimeMs) {}
