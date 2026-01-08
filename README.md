<h1>기존 쇼핑몰 msa 전환</h1>
  
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
* mysql replication 사용
* zero payload 지향 (불가능한 로직 존재)
  
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
* http 표준이 바뀌어서 get 요청에 요청 바디 써도 된다는 입장
* 성능도 기본 구현체인 HttpURLConnection 보다 나음

resilience4j

### 트러블 슈팅
JPA CascadeType.PERSIST 미동작으로 인한 연관관계 데이터 저장 실패
* 배경: 카프카 리스너에서 PK가 있는 엔티티 저장 (공통 mysql 동기화)
* 원인: data jpa save는 엔티티에 PK가 있으면 merge 수행
* merge라서 JPA CascadeType.PERSIST 미동작
* 해결: jpa em.persist 사용

@Transactional 미동작으로 인한 em.flush 미동작
* item -< colorItems (1:N)
* 흐름: syncItem 메서드 실행 (saveItem → saveColorItems)
* 배경: saveColorItems 실패 (item PK가 없다고 롤백됨)
* 원인: syncItem에서 saveItem 직접 호출 → @Transactional 미동작으로 인한 em.flush 미동작 → insert item 쿼리 미발생
* 해결: saveItem에 em.flush 추가

트랜잭션 전파
* 자식 @Transactional





kafka 설정

Resilience4j 설정

주문은 msa 호출이 많다.
상품 조회는 msa 호출이 없다.





