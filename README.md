# 사이드 프로젝트 쿠폰
## 프로젝트 목표
> **대규모 동시 발급 환경에서의 쿠폰 처리 전략을 학습하는 사이드 프로젝트입니다.**

- **동시성 이슈 처리**  
  → 100장의 쿠폰을 3000 명이 동시에 요청할 경우 어떻게 안전하게 분배할 것인가?

- **선착순 발급 처리 방식 학습**  
  → 발급 완료 이후 사용자 요청은 어떻게 처리할 것인가? (예: 대기, 실패 응답, 대체 보상 등)

- **쿠폰 사용 처리 로직**  
  → 발급된 쿠폰의 사용 상태 관리, 중복 사용 방지, 만료 처리 등

- **대기열 사용**  
  → 대기열을 만들어서 선착순 100명만 받게 처리
# ERD
[ERD 보기](https://www.erdcloud.com/d/NFLaBGHvDE9EMjFr2)

<div align="center">
  <img src="https://github.com/user-attachments/assets/8022d7f9-8931-4d41-9da8-9bc347716e6a" width="600" />
</div>

# 순서도

<div align="center">
  <img src="https://github.com/user-attachments/assets/204db76a-5bfc-4cc3-8f1c-ac490177bca2" width="600" />
</div>

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
