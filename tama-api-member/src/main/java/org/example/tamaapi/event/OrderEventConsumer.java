package org.example.tamaapi.event;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.command.MemberCouponRepository;
import org.example.tamaapi.command.MemberRepository;
import org.example.tamaapi.common.exception.OrderFailException;
import org.example.tamaapi.domain.user.Member;
import org.example.tamaapi.domain.user.coupon.MemberCoupon;
import org.example.tamaapi.query.CouponQueryService;
import org.example.tamaapi.feignClient.item.ItemFeignClient;
import org.example.tamaapi.feignClient.item.ItemTotalPriceRequest;
import org.example.tamaapi.feignClient.order.OrderFeignClient;
import org.example.tamaapi.feignClient.order.OrderResponse;
import org.example.tamaapi.feignClient.order.ItemOrderCountResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.example.tamaapi.common.util.ErrorMessageUtil.NOT_FOUND_COUPON;
import static org.example.tamaapi.common.util.ErrorMessageUtil.NOT_FOUND_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderEventConsumer {
    //private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    private Double POINT_ACCUMULATION_RATE = 0.005;

    private final String ORDER_TOPIC = "order_topic";
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponQueryService couponQueryService;

    private final ItemFeignClient itemFeignClient;
    private final OrderFeignClient orderFeignClient;
    /*
    @KafkaListener(topics = ORDER_TOPIC)
    public void consumeOrderCreatedEvent(OrderCreatedEvent event){

        OrderResponse order = orderFeignClient.getOrder(event.orderId());

        Member member = memberRepository.findById(event.memberId())
                .orElseThrow(() -> new OrderFailException(NOT_FOUND_MEMBER));
        //사용한 포인트 차감
        member.minusPoint(order.getUsedPoint());

        MemberCoupon memberCoupon = null;

        //주문 후에, 쿠폰 처리하는게 이상적이자만, saveOrder에 memberCoupon 넘겨야해서 미리 처리함
        //주문 예외나면 쿠폰 롤백되서 미리 처리해도 괜찮음
        Long memberCouponId = order.getMemberCouponId();
        if (memberCouponId != null) {
            memberCoupon = memberCouponRepository.findById(memberCouponId)
                    .orElseThrow(() -> new OrderFailException(NOT_FOUND_COUPON));
            memberCoupon.changeIsUsed(true);
        }

        List<ItemOrderCountResponse> orderItems = orderFeignClient.getOrderItems(event.orderId());

        List<ItemTotalPriceRequest> itemTotalPriceRequests = new ArrayList<>();
        for (ItemOrderCountResponse orderItem : orderItems) {
            itemTotalPriceRequests.add(new ItemTotalPriceRequest(orderItem.colorItemSizeStockId(), orderItem.count()));
        }

        int orderItemsPrice = itemFeignClient.getTotalPrice(itemTotalPriceRequests);
        int couponPrice = couponQueryService.getCouponPrice(memberCouponId, orderItemsPrice);
        int usedPoint = order.getUsedPoint();

        //포인트 적립
        int accumulatedPoint = (int) ((orderItemsPrice - couponPrice - usedPoint) * POINT_ACCUMULATION_RATE);
        member.plusPoint(accumulatedPoint);
    }
    */
}
