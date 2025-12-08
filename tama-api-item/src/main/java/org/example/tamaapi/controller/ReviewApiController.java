package org.example.tamaapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tamaapi.common.auth.CustomPrincipal;

import org.example.tamaapi.domain.item.Review;
import org.example.tamaapi.dto.requestDto.item.save.SaveReviewRequest;
import org.example.tamaapi.command.item.ReviewRepository;
import org.example.tamaapi.dto.responseDto.review.SimpleResponse;
import org.example.tamaapi.feignClient.order.OrderFeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewRepository reviewRepository;

    private final OrderFeignClient orderFeignClient;

    @PostMapping("/api/reviews")
    public ResponseEntity<SimpleResponse> saveReview(@Valid @RequestBody SaveReviewRequest saveReviewRequest, @AuthenticationPrincipal CustomPrincipal principal) {
        Long memberId = orderFeignClient.getOrderItemMember(saveReviewRequest.getOrderItemId(), principal.getJwt());
        if(!Objects.equals(memberId, principal.getMemberId()))
            throw new IllegalArgumentException("주문자가 아닙니다.");

        /* api 호출 줄이기 위해 주석 처리
        Member member = memberQueryRepository.findById(principal.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessageUtil.NOT_FOUND_MEMBER));
        */

        Review newReview = new Review(saveReviewRequest.getOrderItemId(), principal.getMemberId(), saveReviewRequest.getRating(), saveReviewRequest.getComment(),
                saveReviewRequest.getHeight(), saveReviewRequest.getWeight());
        reviewRepository.save(newReview);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse("저장 완료"));
    }

}
