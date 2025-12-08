package org.example.tamaapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.common.auth.CustomPrincipal;
import org.example.tamaapi.common.exception.UnauthorizedException;
import org.example.tamaapi.common.util.ErrorMessageUtil;
import org.example.tamaapi.domain.order.Order;
import org.example.tamaapi.domain.order.OrderItem;
import org.example.tamaapi.dto.feign.OrderResponse;
import org.example.tamaapi.dto.feign.ItemOrderCountResponse;

import org.example.tamaapi.query.order.OrderItemQueryRepository;
import org.example.tamaapi.query.order.OrderQueryRepository;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.example.tamaapi.common.util.ErrorMessageUtil.NOT_FOUND_ORDER_ITEM;

@RestController
@RequiredArgsConstructor
public class FeignApiController {

    private final OrderItemQueryRepository orderItemQueryRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/orders/{orderId}/item")
    public List<ItemOrderCountResponse> getOrderItems(@PathVariable Long orderId, @AuthenticationPrincipal CustomPrincipal principal) {
        List<OrderItem> orderItems = orderItemQueryRepository.findAllByOrderId(orderId);

        //본인 인증
        Long memberId = orderItems.get(0).getOrder().getMemberId();
        if(!memberId.equals(principal.getMemberId()))
            throw new AuthorizationDeniedException(ErrorMessageUtil.ACCESS_DENIED);

        List<ItemOrderCountResponse> itemOrderCountRespons = orderItems.stream().map(ItemOrderCountResponse::new).toList();
        return itemOrderCountRespons;
    }

    @GetMapping("/api/orders/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId, @AuthenticationPrincipal CustomPrincipal principal) {
        Order order = orderQueryRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessageUtil.NOT_FOUND_ORDER));

        //본인 인증
        Long memberId = order.getMemberId();
        if(!memberId.equals(principal.getMemberId()))
            throw new AuthorizationDeniedException(ErrorMessageUtil.ACCESS_DENIED);

        return new OrderResponse(order);
    }

    @GetMapping("/api/ordersItem/{orderItemId}/member")
    public Long getOrderItemMember(@PathVariable Long orderItemId, @AuthenticationPrincipal CustomPrincipal principal) {
        OrderItem orderItem = orderItemQueryRepository.findWithOrderById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_ORDER_ITEM));

        return orderItem.getOrder().getMemberId();
    }

}
