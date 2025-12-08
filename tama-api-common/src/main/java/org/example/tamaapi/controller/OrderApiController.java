package org.example.tamaapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tamaapi.auth.CustomPrincipal;
import org.example.tamaapi.aspect.PreAuthentication;
import org.example.tamaapi.dto.requestDto.CustomPageRequest;
import org.example.tamaapi.dto.responseDto.CustomPage;

import org.example.tamaapi.repository.order.query.dto.GuestOrderResponse;
import org.example.tamaapi.repository.order.query.dto.MemberOrderResponse;
import org.example.tamaapi.repository.order.query.dto.AdminOrderResponse;
import org.example.tamaapi.repository.order.query.OrderQueryRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.example.tamaapi.util.ErrorMessageUtil.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderApiController {

    private final OrderQueryRepository orderQueryRepository;

    //멤버 주문 조회
    @GetMapping("/api/orders/member")
    public CustomPage<MemberOrderResponse> orders(@AuthenticationPrincipal CustomPrincipal principal, @Valid @ModelAttribute CustomPageRequest customPageRequest) {
        if (principal == null)
            throw new IllegalArgumentException("액세스 토큰이 비었습니다.");
        //조회라 굳이 멤버 존재 체크 안필요
        return orderQueryRepository.findMemberOrdersWithPaging(customPageRequest, principal.getMemberId());
    }

    //비로그인 주문 조회
    @GetMapping("/api/orders/guest")
    public GuestOrderResponse guestOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        // "Basic YWRtaW46cGFzc3dvcmQ=" 형태 → Base64 디코딩
        if (authHeader == null || !authHeader.startsWith("Basic "))
            throw new IllegalArgumentException(INVALID_HEADER);

        String base64Credentials = authHeader.substring(6); // "Basic " 이후의 값 추출
        String decodedCredentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);

        // "orderId:buyerName" 형태에서 분리
        String[] values = decodedCredentials.split(":", 2);
        if (values.length != 2)
            throw new IllegalArgumentException(INVALID_HEADER);

        String buyerName = values[0];
        Long orderId = Long.parseLong(values[1]);

        GuestOrderResponse guestOrderResponse = orderQueryRepository.findGuestOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_ORDER));

        if (!guestOrderResponse.getGuestName().equals(buyerName))
            throw new IllegalArgumentException(NOT_FOUND_ORDER);

        return guestOrderResponse;
    }

    //모든 주문 조회
    @GetMapping("/api/orders")
    @PreAuthentication
    @PreAuthorize("hasRole('ADMIN')")
    public CustomPage<AdminOrderResponse> orders(@Valid @ModelAttribute CustomPageRequest customPageRequest) {
        return orderQueryRepository.findAdminOrdersWithPaging(customPageRequest);
    }

}
