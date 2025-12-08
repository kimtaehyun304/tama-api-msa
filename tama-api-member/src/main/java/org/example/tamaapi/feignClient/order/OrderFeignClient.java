package org.example.tamaapi.feignClient.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:5001")
public interface OrderFeignClient {

    @GetMapping("/api/orders/{orderId}/item")
    List<ItemOrderCountResponse> getOrderItems(@PathVariable Long orderId);

    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrder(@PathVariable Long orderId);

}
