# Coupon Queue API 코드 리뷰

## 개요
쿠폰 발급 대기열 관련 `CouponQueueController`와 `CouponQueueService`의 코드 리뷰입니다. 전반적으로 대기열 시스템의 기본 기능은 잘 구현되어 있으나, 성능 및 안정성 향상을 위해 개선이 필요한 몇 가지 중요한 사항들이 있습니다.

## CouponQueueController

### 잘된 점
- **일관된 응답 구조**: `ApiResponse`를 사용하여 API 응답 형식을 통일한 점이 좋습니다.
- **명확한 API 문서**: Swagger 어노테이션(`@Operation`, `@Tag`)을 사용하여 API의 기능과 명세를 명확하게 문서화했습니다.

### 개선 제안
1. **포괄적인 예외 처리**: 모든 메소드에서 `catch (Exception e)`를 사용하여 예외를 처리하고 있습니다. 이는 잠재적인 문제의 원인을 파악하기 어렵게 만듭니다. 각 서비스 메소드에서 발생할 수 있는 특정 예외(예: `IllegalArgumentException`)를 정의하고, 컨트롤러에서 이를 개별적으로 처리하여 더 명확한 오류 메시지를 반환하는 것이 좋습니다.
2. **오타 수정**: `joinQueue` 메소드 내 `switch` 문에서 `"WATING"`이라는 오타가 있습니다. `QueueStateType.WAITING`의 메시지를 사용하고 있으므로 `"WAITING"`으로 수정해야 합니다.
    ```java
    // 수정 전
    case "WATING" -> String.format(QueueStateType.WAITING.getMessage(), position.position());

    // 수정 후
    case "WAITING" -> String.format(QueueStateType.WAITING.getMessage(), position.position());
    ```
3. **관리자 API 분리**: `init-stock`과 같이 재고를 직접 조작하는 기능은 일반 사용자에게 노출되어서는 안 됩니다. 해당 API는 별도의 관리자용 컨트롤러로 분리하고, 권한 확인(Authentication) 로직을 추가하는 것을 권장합니다.

## CouponQueueService

### 잘된 점
- **비동기 처리**: 쿠폰 발급 로직(`processUserCouponAsync`)을 `@Async`를 통해 비동기로 처리하여, 대기열 처리 작업이 다른 요청을 막지 않도록 구현한 점이 훌륭합니다.
- **상태 관리**: 사용자의 상태(대기, 처리 중, 완료, 실패)를 Redis에 명확하게 저장하고 관리하는 패턴은 좋은 설계입니다.

### 개선 제안
1. **(Critical) `KEYS` 명령어 사용 지양**: `processBatch`와 `deleteRedis` 메소드에서 `redisTemplate.keys()`를 사용하고 있습니다. `KEYS` 명령어는 Redis의 모든 키를 순회하므로, 키가 많아질 경우 Redis 서버 전체에 심각한 성능 저하를 유발할 수 있습니다.
    - **대안**: 활성화된 프로모션 ID 목록을 별도의 Redis `Set`으로 관리하세요. 스케줄러는 이 `Set`에 있는 프로모션 ID들만 순회하여 처리하도록 변경해야 합니다.
    ```java
    // 예시: 활성 프로모션 Set 관리
    // 프로모션 시작 시
    redisTemplate.opsForSet().add("active_promotions", promotionId.toString());

    // 스케줄러에서
    Set<String> activePromotions = redisTemplate.opsForSet().members("active_promotions");
    for (String promotionId : activePromotions) {
        processBatchForPromotion(Long.parseLong(promotionId));
    }
    ```
2. **(Major) O(N) 성능의 `getQueuePosition` 메소드**: `getQueuePosition`은 Redis 리스트 전체를 순회하여 사용자의 위치를 찾습니다. 대기열이 길어질수록 이 작업은 매우 느려지며, `addToQueue`와 `getQueueStatus` API의 응답 시간을 저하시키는 병목 지점이 될 것입니다.
    - **대안**: "현재 대기열에 등록되었습니다" 와 같이 순번 정보를 제외한 메시지를 반환하거나, 순번이 꼭 필요하다면 다른 데이터 구조(예: Sorted Set)를 고려해야 합니다. 하지만 가장 간단한 해결책은 해당 기능을 제거하여 성능을 확보하는 것입니다.
3. **상수 관리**: `QueueConstants`를 일부 사용하고 있지만, 여전히 `"COMPLETED"`, `"FAILED"` 같은 문자열 리터럴이 직접 사용되고 있습니다. 모든 상태 문자열을 `QueueConstants` 또는 Enum으로 통합하여 일관성을 유지하고 오타 가능성을 줄이세요.
4. **불필요한 코드 제거**: `processUserCouponAsync` 메소드 내에 주석 처리된 이전 코드는 혼란을 줄 수 있으므로 삭제하는 것이 좋습니다.
5. **중복된 데이터 정리 로직**: `deleteRedis` 스케줄러는 만료된 사용자 상태를 지우기 위해 `KEYS`를 사용합니다. 하지만 이미 `addToQueue`에서 사용자 상태 키에 1시간의 TTL(Time-To-Live)을 설정했기 때문에 Redis가 자동으로 키를 삭제해줍니다. 이중으로 정리할 필요가 없으므로 `deleteRedis` 메소드는 삭제해도 무방해 보입니다.

## 요약
현재 코드는 쿠폰 대기열의 핵심 로직을 잘 담고 있습니다. 하지만 실제 대규모 트래픽 환경에서는 Redis의 성능을 저하시킬 수 있는 `KEYS` 명령어와 O(N) 복잡도의 로직이 심각한 문제를 일으킬 수 있습니다. 위에서 제안된 **`KEYS` 명령어 제거**와 **`getQueuePosition` 로직 개선**을 최우선으로 진행하는 것을 강력히 권장합니다.