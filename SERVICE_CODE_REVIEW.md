#  서비스 레이어 코드 리뷰

`C:/side_project/src/main/java/com/example/sideProject/` 경로 아래의 모든 `Service` 파일에 대한 코드 리뷰입니다.

---

## 1. `copon/Service/CouponService.java`

쿠폰 생성, 목록 조회, 발급, 사용 등 쿠폰과 관련된 다양한 기능을 담당하는 서비스입니다. DB 부하 분산을 위해 메모리 큐를 활용한 비동기 저장을 시도한 점이 특징입니다.

### ✔️ 잘된 점 (Strengths)

1.  **DB 쓰기 부하 분산**: 쿠폰 발급 정보를 메모리 큐(`ConcurrentLinkedQueue`)에 모아두었다가, 스케줄러를 통해 주기적으로 일괄 저장(`saveAll`)하는 방식을 사용했습니다. 이는 대규모 트래픽 발생 시 DB 쓰기 병목 현상을 완화하고 전체 처리량을 높일 수 있는 좋은 전략입니다.
2.  **안전한 재고 차감 (추정)**: `couponsRepository.decreaseQuantitySafely()`를 통해 재고를 차감하는 것으로 보입니다. 이 메서드가 `UPDATE ... SET quantity = quantity - 1 WHERE ... AND quantity > 0`과 같은 원자적 쿼리로 구현되었다면, Race Condition을 방지하고 데이터 정합성을 보장하는 매우 효과적인 방법입니다.
3.  **체계적인 예외 처리**: `CouponErrorCode` Enum을 활용하여 예외 상황을 관리하고 있습니다. 이는 코드의 가독성을 높이고, 클라이언트에게 일관된 에러 응답을 제공하는 좋은 개발 습관입니다.

### ⚠️ 개선 제안 (Areas for Improvement)

1.  **인메모리 큐의 데이터 유실 위험 (심각)**
    -   **문제점**: `ConcurrentLinkedQueue`는 애플리케이션 메모리 위에서만 동작합니다. 만약 스케줄러가 DB에 데이터를 저장하기 전에 애플리케이션이 장애로 종료되거나 재시작되면, **큐에 쌓여있던 모든 쿠폰 발급 데이터가 영구적으로 손실**됩니다. 사용자는 발급 성공 응답을 받았지만 실제 쿠폰은 발급되지 않은 심각한 데이터 불일치 문제가 발생합니다.
    -   **개선안**: 데이터의 영속성을 보장하기 위해 **Redis, RabbitMQ, Kafka**와 같은 외부 메시지 큐 시스템 도입을 강력히 권장합니다. 다른 서비스에서 이미 Redis를 사용하고 있으므로, 해당 로직을 Redis의 List나 Stream을 활용하는 방식으로 통합하는 것이 바람직합니다.

2.  **쿠폰 사용 로직 오류 (심각)**
    -   **문제점**: `useCoupon` 메서드 마지막의 `CouponIssues.builder()...build()` 코드는 새로운 객체를 생성할 뿐, **데이터베이스의 상태를 전혀 변경하지 않습니다.** 결과적으로 쿠폰의 `isUsed` 상태가 `true`로 업데이트되지 않아, **사용자가 쿠폰을 무제한으로 중복 사용할 수 있는 치명적인 버그**가 존재합니다.
    -   **개선안**:
        1.  `couponIssuesRepository.findByCouponIdAndUserId(...)` 등으로 정확한 발급 내역 엔티티를 조회합니다.
        2.  조회된 엔티티의 상태를 변경하는 메서드(예: `entity.markAsUsed()`)를 호출합니다.
        3.  `@Transactional` 환경 하에서 변경된 엔티티를 `save()`하여 DB에 업데이트(Dirty Checking)해야 합니다.

3.  **전체 사용자 대상 발급의 성능 문제**
    -   **문제점**: `allGetCoupon` 메서드에서 `userRepository.findAll()`을 호출하면 DB의 **모든 사용자**를 한 번에 메모리로 가져옵니다. 사용자 수가 많아지면 **OutOfMemoryError가 발생하여 서버가 다운**될 수 있습니다.
    -   **개선안**: 대용량 데이터는 반드시 **배치(Batch) 처리**가 필요합니다. JPA의 `Pageable`을 사용하여 사용자 목록을 일정 크기(예: 1,000명)의 페이지 단위로 나누어 조회하고, 각 배치마다 쿠폰을 발급하고 저장하는 방식으로 리팩토링해야 합니다.

---