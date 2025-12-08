package org.example.tamaapi.event;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.command.item.ItemService;

import org.example.tamaapi.feignClient.order.OrderFeignClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {
    //private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String ORDER_TOPIC = "order_topic";
    private final ItemService itemService;
    private final OrderFeignClient orderFeignClient;
    /*
    //재고 차감
    @KafkaListener(topics = ORDER_TOPIC)
    public void consumeOrderCreatedEvent(OrderCreatedEvent event){
        List<OrderItemFeignResponse> orderItems = orderFeignClient.getOrderItems(event.orderId());
        for (OrderItemFeignResponse orderItem : orderItems) {
            itemService.removeStock(orderItem.colorItemSizeStockId(), orderItem.count());
        }
    }
     */
}
