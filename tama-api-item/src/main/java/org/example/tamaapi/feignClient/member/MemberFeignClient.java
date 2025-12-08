package org.example.tamaapi.feignClient.member;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service", url = "http://localhost:5003")
public interface MemberFeignClient {

    @GetMapping("/coupon/{memberCouponId}/price")
    int getCouponPrice(
            @PathVariable("memberCouponId") Long memberCouponId,
            @RequestParam("orderItemsPrice") int orderItemsPrice
    );

    @PutMapping("/coupon/{memberCouponId}/")
    void useCouponAndPoint(@PathVariable Long memberCouponId);

    @PutMapping("/coupon/{memberCouponId}/rollback")
    void rollbackCouponAndPoint(@PathVariable Long memberCouponId);

    @GetMapping("/{memberId}")
    Authority findAuthority(@PathVariable Long memberId);



}
