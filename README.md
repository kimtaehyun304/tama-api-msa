<h1>기존 쇼핑몰 msa 전환 / 2025-11 ~</h1>
  
<img width="760" height="171" alt="Image" src="https://github.com/user-attachments/assets/0a2e8777-7e84-4741-88a2-71e3b563a1cb" />

### 프로젝트 주소
* 주문 msa: https://github.com/kimtaehyun304/tama-api-order
* 상품 msa: https://github.com/kimtaehyun304/tama-api-item
* 회원 msa: https://github.com/kimtaehyun304/tama-api-member
* 공통 monolith: https://github.com/kimtaehyun304/tama-api-common
* 지연 이벤트 msa: https://github.com/kimtaehyun304/tama-api-mq-delay

### 기술 스택
*  nginx 1.29 (gateway), openFeign 13, resilience4j 2, confluent kafka 7, docker-compose
*  각 msa에서 토큰 인증을 해서 gateway는 라우팅만 하려고 nginx 선택
*  database per service 패턴 + 폴링 기반 트랜잭셔널 아웃박스 패턴 
*  sql 조인을 위한 공통 db (테이블과 컬럼을 최소화)
*  상대 서버 부하시 회복 시간을 벌기위해, 이벤트를 지연 토픽에 발행
*  주문 로직은 api 호출 방식, 보상 트랜잭션은 이벤트 (링크)

### 이슈 해결 & 성능 개선
*  openFeign http 구현체를 Apache HTTP Client 5로 변경
*  kafka 메시지 소비 순서 보장, 메시지 유실 및 중복 방지, 수동 커밋 설정 (링크)
*  주문 서버 스케일 아웃시 아웃박스 폴링에서 경합을 방지하려고 스킵락 사용  
ㄴ  kafka.send()는 CompletableFuture를 통해 비동기 기반 병렬로 실행  
*  api 호출이 타임아웃으로 실패할 경우, db 반영은 됐을 수도 있어서 로그로 확인  
ㄴ 재고 update 후, 로그 테이블에 기록 저장 (한 트랜잭션이라 신뢰 가능)  
*  서킷브레이커 ignore-exception에 NotEnoughStockException 등록  
ㄴ 주문 서버의 itemFeignFallback에서 http 응답을 보고 알맞은 예외로 변환  
*  주문 완료전에 재고만 차감되고 주문 서버가 down 될걸 대비하여, 재고 롤백 스케줄러 사용
*  data jpa save()는 pk가 있으면 merge로 동작해서 대신 em.persist() 사용  
