package org.example.tamaapi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.tamaapi.domain.user.Authority;
import org.example.tamaapi.domain.user.Member;
import org.example.tamaapi.domain.user.coupon.Coupon;
import org.example.tamaapi.domain.user.coupon.MemberCoupon;
import org.example.tamaapi.query.MemberCouponQueryRepository;
import org.example.tamaapi.query.MemberQueryRepository;

import org.example.tamaapi.command.CouponRepository;
import org.example.tamaapi.command.MemberCouponRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final MemberCouponQueryRepository memberCouponQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;


    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    //체험용 계정에 쿠폰 발급 (다 썼을 경우)
    //체험용 계정은 면접관이 기능 체험할때 쓰라고 만든 계정
    public void giveCoupon() {
        Member experienceAccount = memberQueryRepository.findAllByAuthority(Authority.MEMBER).get(1);
        boolean isAllCouponsUsed = !memberCouponQueryRepository.existsByMemberIdAndIsUsedIsFalse(experienceAccount.getId());

        if(isAllCouponsUsed){
            List<Coupon> coupons = couponRepository.findAllById(List.of(1L, 2L, 3L, 4L, 5L, 6L));
            memberCouponRepository.save(new MemberCoupon(coupons.get(0), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(1), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(2), experienceAccount, false));

            memberCouponRepository.save(new MemberCoupon(coupons.get(3), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(4), experienceAccount, false));
            memberCouponRepository.save(new MemberCoupon(coupons.get(5), experienceAccount, false));
        }
    }
}
