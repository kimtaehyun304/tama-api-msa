package org.example.tamaapi.feignClient.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:5001")
public interface OrderFeignClient {

    @GetMapping("/api/orders/{orderId}/item")
    List<OrderItemFeignResponse> getOrderItems(@PathVariable Long orderId);

    @GetMapping("/api/ordersItem/{orderItemId}/member")
    Long getOrderItemMember(@PathVariable Long orderItemId, @RequestHeader("Authorization") String jwt);

}
