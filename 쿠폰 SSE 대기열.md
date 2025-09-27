# 실시간 알림을 위한 SSE(Server-Sent Events) 도입 가이드

이 문서는 `COUPON_QUEUE_ARCHITECTURE.md`에서 정의한 Redis 대기열 아키텍처를 기반으로, 사용자에게 쿠폰 발급 완료 상태를 실시간으로 알려주기 위해 SSE(Server-Sent Events)를 도입하는 방법을 안내합니다.

## 1. 아키텍처 개선 목표

### 1.1. 기존 아키텍처의 한계

현재의 Redis 대기열 방식은 서버의 부하를 분산하고 빠른 응답을 보장하지만, 사용자 경험(UX) 관점에서는 한계가 있습니다.

-   **상태 불확실성**: 사용자는 '쿠폰 발급 요청'이 성공했다는 응답을 받지만, 이는 '대기열에 등록됨'을 의미할 뿐, 실제 쿠폰이 발급 완료된 시점은 알 수 없습니다.
-   **불필요한 확인**: 사용자는 쿠폰이 발급되었는지 확인하기 위해 '내 쿠폰함' 페이지를 반복적으로 새로고침해야 합니다.

### 1.2. SSE 도입을 통한 개선

SSE는 서버가 클라이언트에게 단방향으로 데이터를 푸시(Push)할 수 있는 HTTP 기반 기술입니다. 이를 활용하여 비동기 작업의 최종 완료 시점을 사용자에게 능동적으로 알려줄 수 있습니다.

-   **실시간 피드백**: 쿠폰 발급이 최종 완료되는 시점에 서버가 클라이언트에게 알림을 보내, 사용자가 즉시 상태를 인지할 수 있도록 합니다.
-   **UX 향상**: '발급 중...'과 같은 중간 상태를 명확히 표시하고, 완료 시 '발급 완료!'로 UI를 자동 변경하여 사용자 편의성을 극대화합니다.

## 2. SSE 결합 아키텍처

기존 대기열 구조에 SSE 연결 관리 및 이벤트 발행/구독 로직을 추가합니다.

1.  **(Client → Server)**: 클라이언트는 쿠폰 발급 페이지에 진입 시, 서버의 `/subscribe` 엔드포인트로 SSE 연결을 요청합니다. 동시에 '쿠폰 받기' 요청을 API로 보냅니다.
2.  **(Server)**: `CouponService`는 기존과 같이 Redis 재고를 차감하고, 발급 정보를 대기열에 추가한 뒤 즉시 "요청 접수" 응답을 보냅니다.
3.  **(Background Consumer)**: `CouponIssueQueueConsumer`는 대기열의 작업을 처리하여 DB에 쿠폰을 저장합니다.
4.  **(Event Publish)**: DB 저장에 성공하면, Consumer는 **"쿠폰 발급 완료" 이벤트**를 **Redis Pub/Sub** 채널로 발행(Publish)합니다. 이 이벤트에는 알림을 받을 대상(`userId`) 정보가 포함됩니다.
5.  **(Event Subscribe & Push)**: 이벤트를 구독(Subscribe)하고 있던 `RedisMessageSubscriber`는 메시지를 수신합니다.
6.  **(Server → Client via SSE)**: `RedisMessageSubscriber`는 `SseEmitterService`를 통해 해당 `userId`의 SSE 연결(Emitter)을 찾아, "발급 완료" 데이터를 클라이언트로 전송(Push)합니다.

### 처리 흐름도

```
[Client] <-(1) SSE 연결/수신-> [SseController] -> [SseEmitterService] (Emitter 저장)
   |                                                    ^
   | (2) 쿠폰 발급 API 호출                               | (6) Emitter로 데이터 전송
   V                                                    |
[CouponController] -> [CouponService] --(3)--> [Redis Queue]
   |                                                    |
   <--(2.1) "접수 완료" 응답                              | (4. 비동기 처리)
                                                        V
                                            [CouponIssueQueueConsumer]
                                                        |
                                                        | (5) DB 저장 후 이벤트 발행
                                                        V
                                                  [Redis Pub/Sub]
                                                        |
                                                        | (5.1 이벤트 수신)
                                                        V
                                            [RedisMessageSubscriber]
```

## 3. 구현 코드

### 3.1. SseEmitter 관리

사용자별 SSE 연결(`SseEmitter`)을 관리하고 알림을 전송하는 서비스와 컨트롤러입니다.

#### `SseEmitterService.java` (신규 생성)

```java
package com.example.sideProject.copon.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseEmitterService {

    // 사용자 ID를 키로 SseEmitter를 저장
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 타임아웃을 매우 길게 설정
        this.emitters.put(userId, emitter);

        // 연결 종료 시 Emitter 제거
        emitter.onCompletion(() -> this.emitters.remove(userId));
        emitter.onTimeout(() -> this.emitters.remove(userId));

        // 연결 직후, 더미 데이터 전송 (503 Service Unavailable 방지)
        sendToClient(emitter, "connect", "SSE connected. userId=" + userId);

        return emitter;
    }

    public void sendNotification(Long userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            sendToClient(emitter, eventName, data);
        }
    }

    private void sendToClient(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            log.error("Error sending SSE event: {}", e.getMessage());
            emitter.complete(); // 오류 발생 시 연결 종료
        }
    }
}
```

#### `SseController.java` (신규 생성)

```java
package com.example.sideProject.copon.controller;

import com.example.sideProject.copon.Service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterService sseEmitterService;

    // 클라이언트가 SSE 연결을 요청하는 엔드포인트
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        return sseEmitterService.subscribe(userId);
    }
}
```

### 3.2. Redis Pub/Sub 설정 및 이벤트 발행/구독

`CouponIssueQueueConsumer`와 `SseEmitterService` 간의 통신을 위해 Redis Pub/Sub을 사용합니다.

#### `RedisConfig.java` (수정 또는 신규 생성)

Redis Pub/Sub을 위한 리스너 컨테이너와 어댑터를 Bean으로 등록합니다.

```java
package com.example.sideProject.config;

import com.example.sideProject.copon.Service.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    public static final String COUPON_ISSUE_COMPLETE_TOPIC = "coupon-issue-complete";

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic(COUPON_ISSUE_COMPLETE_TOPIC));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageSubscriber subscriber) {
        // onMessage 메서드가 호출되도록 설정
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}
```

#### `CouponIssueQueueConsumer.java` (수정)

DB 저장 후, Redis 채널로 이벤트를 발행하는 로직을 추가합니다.

```diff
--- a/C:/side_project/src/main/java/com/example/sideProject/copon/Service/CouponIssueQueueConsumer.java
+++ b/C:/side_project/src/main/java/com/example/sideProject/copon/Service/CouponIssueQueueConsumer.java
@@ -7,6 +7,7 @@
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import lombok.RequiredArgsConstructor;
+import com.example.sideProject.config.RedisConfig;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.data.redis.core.StringRedisTemplate;
 import org.springframework.scheduling.annotation.Scheduled;
@@ -24,6 +25,7 @@
     private final StringRedisTemplate redisTemplate;
     private final CouponRepository couponRepository;
     private final ObjectMapper objectMapper;
+    private final RedisConfig redisConfig;
 
     private static final String QUEUE_KEY = "coupon:issue-queue";
 
@@ -44,6 +46,10 @@
                         CouponIssueRequest request = objectMapper.readValue(requestJson, CouponIssueRequest.class);
                         Coupon coupon = Coupon.issued(request.promotionId(), request.userId());
                         couponRepository.save(coupon);
+
+                        // DB 저장 성공 후, Redis Pub/Sub으로 이벤트 발행
+                        String message = request.userId() + ":" + request.promotionId();
+                        redisTemplate.convertAndSend(RedisConfig.COUPON_ISSUE_COMPLETE_TOPIC, message);
                     } catch (JsonProcessingException e) {
                         log.error("Failed to process coupon issue request from queue: {}", requestJson, e);
                         // 실패한 요청에 대한 처리 (e.g., Dead Letter Queue로 이동)

```

#### `RedisMessageSubscriber.java` (신규 생성)

이벤트를 구독하여 `SseEmitterService`를 통해 클라이언트에게 알림을 보냅니다.

```java
package com.example.sideProject.copon.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final SseEmitterService sseEmitterService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Received message from Redis: {}", body);

        // 메시지 파싱 (e.g., "userId:promotionId")
        try {
            String[] parts = body.split(":");
            Long userId = Long.parseLong(parts[0]);
            Long promotionId = Long.parseLong(parts[1]);

            // 해당 사용자에게 SSE 알림 전송
            sseEmitterService.sendNotification(userId, "issue-complete", "쿠폰 발급이 완료되었습니다. (프로모션 ID: " + promotionId + ")");
        } catch (Exception e) {
            log.error("Error processing message from Redis: {}", body, e);
        }
    }
}
```

## 4. 결론

SSE와 Redis Pub/Sub을 도입함으로써, 기존 비동기 아키텍처의 성능적 이점은 그대로 유지하면서 사용자에게 실시간 피드백을 제공하는, 한 단계 더 발전된 시스템을 구축할 수 있습니다.

이는 쿠폰 발급뿐만 아니라, 주문 처리, 파일 변환 등 처리 시간이 긴 다양한 비동기 작업의 진행 상태를 사용자에게 알려주는 데에도 동일하게 적용할 수 있는 강력한 패턴입니다.