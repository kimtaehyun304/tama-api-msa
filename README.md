<h1>기존 쇼핑몰 msa 전환</h1>

기존 쇼핑몰: https://github.com/kimtaehyun304/tama-api  
db 수평 확장: https://github.com/kimtaehyun304/tama-api-replication  
주문 msa: https://github.com/kimtaehyun304/tama-api-order  
상품 msa: https://github.com/kimtaehyun304/tama-api-item  
회원 msa: https://github.com/kimtaehyun304/tama-api-member  
읽기 msa: https://github.com/kimtaehyun304/tama-api-common 

<img width="500" height="500" alt="Image" src="https://github.com/user-attachments/assets/8e3e0f7b-c72f-4b91-87e8-ef0004ee4fc4" />


### 프로젝트 스택
* 스프링 부트3, data jpa, mysql 8
* nginx gateway, openFeign, kafka  

### MSA 구성
* 주문 / 상품 / 회원
* 조인 쿼리용 msa (api composition 대체)

### 기술 선택 근거
nginx gateway
* 단순 라우팅이 필요한거라, sping cloud gateway 선택 X
* jwt 인증을 각 msa에서 수행 (이미 만든 api의 url과 로직을 수정하긴 어려움)

kafka
* 조인 쿼리용 DB 동기화를 위해 사용
* 동기화 데이터 정합성을 위해, API 호출 대신 메시지 시스템 사용 (메시지 무손실)
* 주의 사항 앎 ex) 이벤트 소비 순서, 중복, 수동 커밋

openFeign
* API 메소드처럼 만들 수 있어서 가독성 좋음
* @GetMapping에 @RequestBody를 쓰기위해, HttpURLConnection → Apache HTTP Client 5 변경
* HTTP 표준이 바뀌어서 GET 요청에 요청 바디 써도 된다고 생각
* 성능도 Apache HTTP Client 5이 더 나음

api 동기 호출 아키텍처 (try-catch)
* 이벤트 기반 아키텍처 보다 간단 
* 주문 실패로 인한 재고 롤백은 이벤트 발행
* 최종 일관성 방식이 아니라 위해 폴링 안해도 됨

feignApi는 내부 사설 IP만 허용


