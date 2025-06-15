# 사이드 프로젝트 쿠폰
## 프로젝트 목표
> **대규모 동시 발급 환경에서의 쿠폰 처리 전략을 학습하는 사이드 프로젝트입니다.**

- **동시성 이슈 처리**  
  → 예: 50장의 쿠폰을 수천 명이 동시에 요청할 경우 어떻게 안전하게 분배할 것인가?

- **선착순 발급 처리 방식 학습**  
  → 발급 완료 이후 사용자 요청은 어떻게 처리할 것인가? (예: 대기, 실패 응답, 대체 보상 등)

- **쿠폰 사용 처리 로직**  
  → 발급된 쿠폰의 사용 상태 관리, 중복 사용 방지, 만료 처리 등
# ERD
[ERD 보기](https://www.erdcloud.com/d/NFLaBGHvDE9EMjFr2)

<div align="center">
  <img src="https://github.com/user-attachments/assets/8022d7f9-8931-4d41-9da8-9bc347716e6a" width="600" />
</div>

# 순서도

<div align="center">
  <img src="https://github.com/user-attachments/assets/204db76a-5bfc-4cc3-8f1c-ac490177bca2" width="600" />
</div>
