package org.example.tamaapi.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tamaapi.domain.BaseEntity;
import org.example.tamaapi.domain.user.coupon.MemberCoupon;
import org.example.tamaapi.domain.user.Guest;
import org.example.tamaapi.domain.user.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Embedded
    private Guest guest;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_coupon_id", unique = true)
    private MemberCoupon memberCoupon;

    //매번 계산할 수 있지만 코드가 복잡해져서 필드 만듬
    //할인 금액이 의미있는 정보이기도 함
    private int usedCouponPrice;

    private int usedPoint;

    //배송비는 달라질수 있음. 또한 얼마 이상 구매시 무료가 되는 기준 금액도 달라질수 있음
    private int shippingFee;

    //포트원 결제 번호 (문자열)
    private String paymentId;

    //cascade는 insert 여러번 실행되서 jdbcTemplate 사용
    @OneToMany(mappedBy = "order")
    //@BatchSize(size = 1000) osiv off
    private List<OrderItem> orderItems = new ArrayList<>();



}

