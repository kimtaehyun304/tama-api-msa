package org.example.tamaapi.dto.feign;

import org.example.tamaapi.domain.order.Order;

import static org.example.tamaapi.command.order.OrderService.POINT_ACCUMULATION_RATE;

public class OrderResponse {

    private Long memberId;

    private Long memberCouponId;

    private int usedCouponPrice;

    private int usedPoint;

    private int shippingFee;

    private Double pointAccumulationRate;

    public OrderResponse(Order order){
        this.memberId = order.getMemberId();
        this.memberCouponId = order.getMemberCouponId();
        this.usedCouponPrice = order.getUsedCouponPrice();
        this.usedPoint = order.getUsedPoint();
        this.shippingFee = order.getShippingFee();
        this.pointAccumulationRate = POINT_ACCUMULATION_RATE;
    }
}
