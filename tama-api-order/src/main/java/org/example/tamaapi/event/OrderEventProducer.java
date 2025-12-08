package org.example.tamaapi.event;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.domain.order.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String ORDER_TOPIC = "order_topic";
    public void produceOrderCreatedEvent(Long orderId, OrderStatus orderStatus){
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(orderId, orderStatus);
        kafkaTemplate.send(ORDER_TOPIC, orderCreatedEvent);
    }

}
