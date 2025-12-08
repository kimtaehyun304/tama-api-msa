package org.example.tamaapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.command.CouponService;
import org.example.tamaapi.common.auth.CustomPrincipal;
import org.example.tamaapi.common.util.ErrorMessageUtil;
import org.example.tamaapi.domain.user.Authority;
import org.example.tamaapi.query.CouponQueryService;
import org.example.tamaapi.query.MemberQueryRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class FeignApiController {

    private final CouponQueryService couponQueryService;
    private final CouponService couponService;
    private final MemberQueryRepository memberQueryRepository;

    //쿠폰 소유한 멤버만 보는게 이상적이지만. 안해도 보안 위험 없을 것 같은데
    @GetMapping("/coupon/{memberCouponId}/price")
    public int getCouponPrice(@PathVariable Long memberCouponId, int orderItemsPrice){
        return couponQueryService.getCouponPrice(memberCouponId, orderItemsPrice);
    }

    @PutMapping("/coupon/{memberCouponId}/")
    public void useCouponAndPoint(@PathVariable Long memberCouponId){
        couponService.useCouponAndPoint(memberCouponId);
    }

    @PutMapping("/coupon/{memberCouponId}/rollback")
    public void rollbackCouponAndPoint(@PathVariable Long memberCouponId){
        couponService.rollbackCouponAndPoint(memberCouponId);
    }

    @GetMapping("/authority")
    public Authority findAuthority(@AuthenticationPrincipal CustomPrincipal principal){
        return memberQueryRepository.findAuthorityById(principal.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessageUtil.NOT_FOUND_MEMBER));
    }




}
