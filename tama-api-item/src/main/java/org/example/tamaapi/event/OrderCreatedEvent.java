package org.example.tamaapi.event;

public record OrderCreatedEvent(Long orderId, OrderStatus status) {
}