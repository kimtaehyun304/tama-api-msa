package org.example.tamaapi.command;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.common.exception.OrderFailException;
import org.example.tamaapi.domain.user.Member;
import org.example.tamaapi.domain.user.coupon.MemberCoupon;
import org.example.tamaapi.feignClient.item.ItemFeignClient;
import org.example.tamaapi.feignClient.item.ItemTotalPriceRequest;
import org.example.tamaapi.feignClient.order.OrderFeignClient;
import org.example.tamaapi.feignClient.order.OrderResponse;
import org.example.tamaapi.feignClient.order.ItemOrderCountResponse;
import org.example.tamaapi.query.MemberQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.example.tamaapi.common.util.ErrorMessageUtil.NOT_FOUND_COUPON;
import static org.example.tamaapi.common.util.ErrorMessageUtil.NOT_FOUND_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final OrderFeignClient orderFeignClient;
    private final ItemFeignClient itemFeignClient;
    private final MemberQueryRepository memberQueryRepository;
    private final MemberRepository memberRepository;
    private final MemberCouponRepository memberCouponRepository;

    public void useCouponAndPoint(Long orderId){
        OrderResponse order = orderFeignClient.getOrder(orderId);
        Long memberId = order.getMemberId();


        //주문 후에, 쿠폰 처리하는게 이상적이자만, saveOrder에 memberCoupon 넘겨야해서 미리 처리함
        //주문 예외나면 쿠폰 롤백되서 미리 처리해도 괜찮음
        Long memberCouponId = order.getMemberCouponId();

        MemberCoupon memberCoupon = null;

        if (memberCouponId != null) {
            memberCoupon = memberCouponRepository.findById(memberCouponId)
                    .orElseThrow(() -> new OrderFailException(NOT_FOUND_COUPON));
            memberCoupon.changeIsUsed(true);
        }

        List<ItemTotalPriceRequest> requests = orderFeignClient.getOrderItems(orderId).stream().map(ItemTotalPriceRequest::new).toList();
        int orderItemsPrice = itemFeignClient.getTotalPrice(requests);
        int couponPrice = order.getUsedCouponPrice();
        int usedPoint = order.getUsedPoint();
        Double pointAccumulationRate = order.getPointAccumulationRate();

        int orderPriceUsedCoupon = orderItemsPrice - couponPrice;

        if(memberCoupon != null)
            validateCoupon(memberCoupon, couponPrice, orderItemsPrice);

        validatePoint(usedPoint, memberId, orderPriceUsedCoupon, order.getShippingFee());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new OrderFailException(NOT_FOUND_MEMBER));
        //사용한 포인트 차감
        member.minusPoint(order.getUsedPoint());

        //포인트 적립
        int accumulatedPoint = (int) ((orderItemsPrice - couponPrice - usedPoint) * pointAccumulationRate);
        member.plusPoint(accumulatedPoint);
    }

    public void rollbackCouponAndPoint(Long orderId){
        OrderResponse order = orderFeignClient.getOrder(orderId);
        Long memberId = order.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new OrderFailException(NOT_FOUND_MEMBER));
        //사용한 포인트 차감
        member.minusPoint(order.getUsedPoint());

        //주문 후에, 쿠폰 처리하는게 이상적이자만, saveOrder에 memberCoupon 넘겨야해서 미리 처리함
        //주문 예외나면 쿠폰 롤백되서 미리 처리해도 괜찮음
        Long memberCouponId = order.getMemberCouponId();
        if (memberCouponId != null) {
            MemberCoupon memberCoupon = memberCouponRepository.findByIdAndMemberId(memberCouponId, memberId)
                    .orElseThrow(() -> new OrderFailException(NOT_FOUND_COUPON));
            memberCoupon.changeIsUsed(false);
        }

        List<ItemTotalPriceRequest> requests = orderFeignClient.getOrderItems(orderId).stream().map(ItemTotalPriceRequest::new).toList();
        int orderItemsPrice = itemFeignClient.getTotalPrice(requests);
        int couponPrice = order.getUsedCouponPrice();
        int usedPoint = order.getUsedPoint();
        Double pointAccumulationRate = order.getPointAccumulationRate();
        int accumulatedPoint = (int) ((orderItemsPrice - couponPrice - usedPoint) * pointAccumulationRate);

        //포인트 사용 롤백
        member.plusPoint(order.getUsedPoint());

        //포인트 적립 롤백
        member.minusPoint(accumulatedPoint);
    }

    private void validatePoint(int usedPoint, Long memberId, int orderPriceUsedCoupon, int SHIPPING_FEE) {
        String cancelMsg = null;

        Member member = memberQueryRepository.findById(memberId)
                .orElseThrow(() -> new OrderFailException(NOT_FOUND_MEMBER));

        int serverPoint = member.getPoint();

        if (usedPoint > serverPoint)
            cancelMsg = "보유한 포인트보다 넘게 사용할 수 없습니다.";
        else if (usedPoint > orderPriceUsedCoupon + SHIPPING_FEE)
            cancelMsg = "주문 가격보다 많은 포인트를 사용할 수 없습니다.";

        if (cancelMsg != null)
            throw new OrderFailException(cancelMsg);
    }

    private void validateCoupon(MemberCoupon memberCoupon, int couponPrice, int orderItemsPrice) {
        if (memberCoupon.getCoupon().getExpiresAt().isBefore(LocalDate.now()))
            throw new OrderFailException("쿠폰 유효기간 만료");

        if(memberCoupon.isUsed())
            throw new OrderFailException("이미 사용한 쿠폰입니다.");

        if(couponPrice > orderItemsPrice)
            throw new OrderFailException("쿠폰 금액은 주문 가격보다 넘게 사용할 수 없습니다.");
    }

}
