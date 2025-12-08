package org.example.tamaapi.feignClient.order;


import lombok.Getter;

@Getter
public class OrderResponse {

    private Long memberId;

    private Long memberCouponId;

    private int usedCouponPrice;

    private int usedPoint;

    private int shippingFee;

    private Double pointAccumulationRate;

}
