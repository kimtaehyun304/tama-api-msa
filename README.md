<h1>기존 쇼핑몰 msa 전환</h1>

기존 쇼핑몰: https://github.com/kimtaehyun304/tama-api  
db 수평 확장: https://github.com/kimtaehyun304/tama-api-replication  

### 프로젝트 스택
* 스프링 부트3, data jpa, mysql 8.0.37(복제)  
* openFeign, kafka  

### MSA 종류 
* 주문 / 상품 / 회원
* 조인 쿼리용 서비스 (api composition 대체, kafka 동기화)

### 버전 업그레이드 과정
버전1_로컬 개발 단계  
* nginx gateway / api 호출 기반 아키텍처
* spring cloud gateway 가용성 부족. aws 로드밸런서 쓸 예정 → nginx gateway
* 주문 저장 실패해도 정합성 일치하니 괜찮 → api 호출 기반 아키텍처 적합
* 조인 쿼리용 db 동기화를 api 호출로 하면, 호출 실패 시 정합성 불일치 → 메시징 시스템 적합 

버전2에 SAGA 패턴 적용 예정 (어려워서)
