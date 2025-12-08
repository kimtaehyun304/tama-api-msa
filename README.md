<h1>기존 쇼핑몰 msa 전환</h1>

기존 쇼핑몰: https://github.com/kimtaehyun304/tama-api  
db 수평 확장: https://github.com/kimtaehyun304/tama-api-replication  

스택: 스프링 부트3, data jpa, mysql 8.0.37(복제) / openFeign, kafka  

### MSA 종류 
주문 / 상품 / 회원 / 쿼리용 서비스(api composition 대체, kafka 동기화)

### 버전 업그레이드 과정
버전1_로컬 개발 단계: nginx(spring cloud gateway 대체), api 호출 기반 아키텍처(이벤트 기반 x)
