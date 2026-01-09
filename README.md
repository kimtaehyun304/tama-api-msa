<h1>기존 쇼핑몰 msa 전환 / 2025-11 ~</h1>
  
<img width="800" height="800" alt="Image" src="https://github.com/user-attachments/assets/cb7f9a11-0679-4b42-9759-82267055b42d" />

### 프로젝트 주소
주문 msa: https://github.com/kimtaehyun304/tama-api-order  
상품 msa: https://github.com/kimtaehyun304/tama-api-item  
회원 msa: https://github.com/kimtaehyun304/tama-api-member  
공통 monolith: https://github.com/kimtaehyun304/tama-api-common 

### 기술
* 스프링 부트 3.4 (mvc, security, valid, aop, cache, batch)
* mysql 8, hibernate 6, data jpa, querydsl 5
* nginx gateway, openFeign, kafka, resilience4j 

### 구조
* msa끼리는 rest 방식으로 통신
* kafaka로 데이터 동기화
* 공통 mysql은 테이블 및 컬럼 최소화
* mysql replication 사용 (cqrs)
* atomic integer로 read db들 분산
* zero payload (일부 로직 불가)
  
### msa 기술 선택 근거
nginx gateway
* 단순 라우팅이 필요한거라, sping cloud gateway 선택 X
* jwt 인증을 각 msa에서 수행 (이미 만든 api의 url과 로직을 수정하긴 어려움)

kafka
* 조인 쿼리용 DB 동기화를 위해 사용
* 데이터 정합성을 위해 메시지 시스템 사용 (장애 이후 자동 복구)
* 주의 사항 앎 ex) 이벤트 소비 순서, 중복, 수동 커밋

openFeign
* @GetMapping에 @RequestBody를 쓰기위해, 내부 구현체 Apache HTTP Client 5로 변경
* http 표준이 바뀌어서 get 요청에 요청 바디 써도 된다고 판단
* 성능도 기본 구현체인 HttpURLConnection 보다 나음

### 트러블 슈팅
jpa CascadeType.PERSIST 미동작으로 인한, 연관관계 데이터 저장 실패
* 상황: 카프카 리스너에서 PK가 있는 엔티티 저장 (공통 mysql 동기화)
* 원인: data jpa save는 엔티티에 pk가 있으면 merge 수행
* merge라서 JPA CascadeType.PERSIST 미동작
* 해결: jpa em.persist 사용
  
<a href="https://github.com/kimtaehyun304/tama-api-common/blob/7d452fa1c0eb8f4c2c92fe5e9374cac73e851619/src/main/java/org/example/tamaapi/service/OrderService.java#L43">
  @Transactional 미동작으로 인한 jpa em.flush 미동작
</a>

* item -< colorItems (1:N)
* 흐름: syncItem 메서드 실행 (saveItem → saveColorItems)
* 상황: saveColorItems 실패 (item PK가 없다고 롤백됨)
* 원인: syncItem에서 saveItem 직접 호출 → @Transactional 미동작으로 인한 em.flush 미동작 → insert item 쿼리 미발생
* 해결: saveItem에 em.flush 추가

<a href="https://github.com/kimtaehyun304/tama-api-order/blob/b9185abef225fd19b70eeec796272ff21976da2c/src/main/java/org/example/tamaapi/command/order/OrderService.java#L94">
  커밋 전까지는 해당 트랜젝션에서만 select 가능 → zero payload 불가
</a>

* 흐름: saveMemberOrder 메서드 실행 (saveOrder → decreaseStocks → useCoupon)
* 상황: memberFeignClient.useCoupon(orderId) 호출 실패 (NOT_FOUND_ORDER)
* 원인: 트랜잭션이 아직 안 끝나서 커밋 미동작 → 타 트랜잭션에서는 select 불가
* 해결: orderId 말고 필요한 데이터 전달 ex) usedCouponPrice, orderItemsPrice
  
kafka 설정
* application.yml에서 prducer 직렬화, consumer 역직렬화, listener ack-mode: manual
* 이벤트 소비 순서 보장, 중복 방지: 1토픽 1파티션, consumer group

openFeign 설정
* http 응답을 예외 메시지로 쓰기 위해 ErrorDecoder 오버라이드 ex)응답 직렬화

Resilience4j 설정
* fallback을 등록하지 않으면, NoFallbackAvailableException 발생 (예외를 래핑함)
* fallback 추가하고 예외를 그대로 반환하도록 설정 ex)T타입 반환

### 알게된 점
msa라고 장애 격리가 완벽하진 않다.
* 주문 로직은 상품 msa, 회원 msa 호출 필요 → 두 msa 중 하나만 장애나도 주문 실패
* 상품 조회는 타 msa 호출 없음 → 타 msa 장애나도 상관 없음

공통 코드 관리가 힘들다.
