package org.example.tamaapi.feignClient.member;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "member-service", url = "http://localhost:5003")
public interface MemberFeignClient {

    @GetMapping("/api/member/coupon/{memberCouponId}/price")
    int getCouponPrice(
            @PathVariable("memberCouponId") Long memberCouponId,
            @RequestParam("orderItemsPrice") int orderItemsPrice
    );

    @PutMapping("/api/member/coupon/{memberCouponId}/")
    void useCouponAndPoint(@PathVariable Long memberCouponId);

    @PutMapping("/api/member/coupon/{memberCouponId}/rollback")
    void rollbackCouponAndPoint(@PathVariable Long memberCouponId);

    @GetMapping("/authority")
    Authority findAuthority(@RequestHeader("Authorization") String jwt);

}
