package com.bookstore.dto;

public class OrderResponse {

    private Long orderId;
    private String message;

    public OrderResponse(Long orderId, String message) {
        this.orderId = orderId;
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }
}
