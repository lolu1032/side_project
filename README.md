# 사이드 프로젝트 쿠폰
## 프로젝트 목표
> **대규모 동시 발급 환경에서의 쿠폰 처리 전략을 학습하는 사이드 프로젝트입니다.**

- **동시성 이슈 처리**  
  → 100장의 쿠폰을 6000 명이 동시에 요청할 경우 어떻게 안전하게 분배할 것인가?

- **선착순 발급 처리 방식 학습**  
  → 발급 완료 이후 사용자 요청은 어떻게 처리할 것인가? (예: 대기, 실패 응답, 대체 보상 등)

- **쿠폰 사용 처리 로직**  
  → 발급된 쿠폰의 사용 상태 관리, 중복 사용 방지, 만료 처리 등

- **대기열 사용**  
  → 대기열을 만들어서 선착순 100명만 받게 처리

# 성과
접근 1 : 순간 락 (399TPS) 
  - @Query에 UPDATE을 사용하여 SELECT FOR UPDATE 구현 
단점 
  - 비관적 락 적용으로 처리 지연 발생 
 
접근 2 : Synchronized (898TPS) 
  - HashMap을 통한 단일 서버 인메모리 캐시 사용 
  - synchronized를 사용하여 자바 모니터락을 이용하여 동시성 문제 해결 
  - 재고 감소 후 쿠폰 발급 및 DB 저장 
단점 
  - 단일 서버 캐시다 보니 단일 서버 환경에서는 안정적이나, 다중 서버 환경에서는 적용 불가 
 
접근 3 : AtomicInteger (976TPS) 
  - Compare And Swap 연산으로 while문을 통한 동시성 제어 
  - HashMap을 통한 단일 서버 인메모리 캐시 사용 
단점 
  - 단일 서버 환경에서만 안정적이며, CAS 충돌 발생 시 재시도가 반복되어 극한 동시 요청 시 처리 
지연 가능 
 
접근 4 : Redis (1098TPS) 
  - Redis의 원자적 decrement 연산으로 재고 감소 처리 
  - 재고 부족 시 롤백 처리, DB 저장은 비동기 처리 
  - 최종적 일관성 유지, Redis 즉시 응답 + DB에 최종 저장 
  - Redis 싱글 스레드환경으로 인한 동시성 보장 
  - 단일 서버 캐시에서 문제였던 Scale-Out 환경 보장 
단점 
  - Redis 단일 캐시 서버 의존으로, 장애 발생 시 로그 확인 등 번거로움 발생 
  - 단일 서버 환경에서는 Synchronized 방식이 비용 측면에서 효율적일 수 있음
# 회원 API
## 회원가입
> POST /api/signup

> 신규 사용자 계정 생성
Request Body
```json
{
  "usernmae" : "string",
  "password" : "string"
}
```
## 로그인
> POST /api/login

> 로그인
Request Body
```json
{
  "usernmae" : "string",
  "password" : "string"
}
```
## 회원가입
> POST /api/signup

> 신규 사용자 계정 생성
Request Body
```json
{
  "usernmae" : "string",
  "password" : "string"
}
```
# 쿠폰 API
## 쿠폰 목록 조회
GET /

> 유저가 발급 가능한 쿠폰 리스트를 조회합니다
>
## 쿠폰 받기 (유저 전용)
> POST /api/get

> 이벤트 쿠폰 발급 요청
Request Body

```json
{
  "userId": 1,
  "couponId": 1001
}
```
## 쿠폰 발급 (관리자 전용)
> POST /api/issue

> 어드민이 쿠폰을 생성하고 발급합니다.
Request Body

```json
{
  "title": "10% 할인 쿠폰",
  "quantity": 50,
  "expiresAt": "2025-12-31"
}
```


