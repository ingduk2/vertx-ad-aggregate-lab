# vertx-ad-aggregate-lab

Spring Boot 4 + Vert.x 5 + Kafka로 만드는 실시간 광고 집계 시스템 학습 프로젝트

> 회사 구조의 스케일아웃 한계와 DB flush 병목을 Vert.x Event Loop + Bulk Upsert로 검증하는 것이 목표

---

## 🛠 기술 스택

| 항목          | 버전                |
|-------------|-------------------|
| Java        | 25                |
| Spring Boot | 4.0.x             |
| Vert.x      | 5.0.x             |
| Kafka       | 3.x               |
| Build       | Gradle Kotlin DSL |
| DB          | MariaDB           |
| ORM         | Spring Data JPA   |

---

## 🏗 아키텍처 개요

```
Python Script (부하)
  │
  │  Produce (placementId key)
  ▼
Kafka (ad.request / ad.impression / ad.click)
  │
  │  Consume
  ▼
KafkaConsumerVerticle      ← 토픽별 메시지 수신 & EventBus 전달
  │
  │  EventBus
  ▼
AggregationVerticle        ← Lock Free 집계 (HashMap + periodic flush)
  │
  │  EventBus
  ▼
BulkUpsertWorkerVerticle   ← 블로킹 격리, MariaDB Bulk Upsert
  │
  ▼
MariaDB                    ← placementId + hour 복합키 집계 이력

Spring REST (:8080)        ← 집계 결과 조회 API
```

**집계 키 전략**
- 키: `placementId + yyyyMMddHH`
- 예: `10881244_2026052010` → `{ request: 142, impression: 89, click: 3 }`
- flush 조건: 주기적(N초) + 건수 초과(M건) 동시 적용

---

## 📚 커리큘럼

### Chapter 0 — 환경 세팅 & 프로젝트 구조
> Spring Boot 4 + Vert.x 5 + Kafka 공존시키기

- 0-1. `build.gradle.kts` 의존성 추가 — Vert.x, Kafka Client, MariaDB
- 0-2. Docker Compose로 Kafka + MariaDB 띄우기
- 0-3. 프로젝트 패키지 구조 설계 & README 작성

---

### Chapter 1 — 현재 구조의 문제 이해
> 왜 지금 구조가 스케일아웃이 어려운가

- 1-1. partition 수 = 최대 처리량 한계 — Consumer 늘려도 partition 수 이상은 의미없음
- 1-2. 랜덤 key로 produce → 같은 placementId가 사방에 흩어짐
- 1-3. 서버 N대 + 같은 키 DB 접근 → DeadLock 재현
- 1-4. 진짜 병목은 어디인가 — DB flush 속도
- 1-5. 개선 전략 — placementId partition key + Bulk Upsert + Worker Verticle

---

### Chapter 2 — Kafka Consumer Verticle
> Kafka 메시지를 Vert.x로 받아들이기

- 2-1. Kafka Consumer 기본 — `KafkaConsumer` 설정 & 토픽 구독
- 2-2. Kafka Consumer를 Verticle 안에서 돌리기 — Event Loop 위에서 poll
- 2-3. EventBus 주소 체계 설계 — `ad.request` / `ad.impression` / `ad.click`
- 2-4. Kafka → EventBus 브릿지 — 메시지 수신 후 AggregationVerticle로 전달

---

### Chapter 3 — Lock Free 집계 Verticle
> 단일 스레드의 힘으로 동기화 없이 집계

- 3-1. 왜 Verticle 안의 `HashMap`은 Lock Free인가 — Event Loop 보장
- 3-2. `AggregationVerticle` 설계 — `placementId + hour` 키별 카운터 집계
- 3-3. flush 조건 설계 — 주기적(N초) + 건수 초과(M건)
- 3-4. Vert.x `periodic timer`로 flush 트리거

---

### Chapter 4 — Bulk Upsert Worker Verticle
> DB flush가 진짜 병목 — Worker Verticle로 격리하고 빠르게

- 4-1. Worker Verticle 패턴 — 블로킹 코드를 Event Loop 밖으로
- 4-2. `BulkUpsertWorkerVerticle` 구현 — 배치 단위 MariaDB Upsert
- 4-3. Upsert 전략 — `INSERT ... ON DUPLICATE KEY UPDATE` 설계
- 4-4. EventBus Request-Reply로 flush 결과 수신 & 재시도 패턴
- 4-5. 건별 Upsert vs Bulk Upsert 성능 비교

---

### Chapter 5 — Spring REST 조회 API
> 집계 결과 확인

- 5-1. 집계 결과 조회 API — `placementId + hour` 기준 조회
- 5-2. 실시간 인메모리 스냅샷 조회 — Verticle 현재 버퍼 상태 엔드포인트
- 5-3. WebFlux로 EventBus 응답 대기 — `Mono`로 Verticle 상태 조회

---

### Chapter 6 — 부하 테스트 & 검증
> 실제로 개선이 되는지 확인

- 6-1. Python 스크립트로 Kafka produce — `placementId` 랜덤 분산
- 6-2. 랜덤 key vs placementId key — partition 분산 차이 확인
- 6-3. 건별 Upsert vs Bulk Upsert — DB 처리량 비교
- 6-4. Consumer 수 늘려도 partition 수 이상은 의미없음 — 직접 확인
- 6-5. Verticle 통합 테스트 — `vertx-junit5` & `VertxTestContext`

---

## 📁 패키지 구조

```
src/main/java/com/ad/aggregate/lab/
├── config/
│   ├── VertxConfig.java                  ← Vertx 인스턴스 빈 등록 & Verticle 배포
│   └── KafkaConfig.java
├── verticle/
│   ├── KafkaConsumerVerticle.java        ← Chapter 2
│   ├── AggregationVerticle.java          ← Chapter 3
│   └── BulkUpsertWorkerVerticle.java     ← Chapter 4
├── aggregate/
│   ├── AggregateService.java             ← Spring Service (Worker Verticle에서 호출)
│   ├── AggregateRepository.java
│   └── AdAggregate.java                  ← JPA Entity
└── VertxAdAggregateLabApplication.java
```

---

## 🚀 실행 방법

```bash
# 인프라 먼저
docker-compose up -d

# 앱 실행
./gradlew bootRun
```

---

## 📝 학습 포인트

- **Lock Free 집계** — Verticle 단일 스레드로 `HashMap` 동기화 없이 집계
- **partition key 전략** — 랜덤 key vs placementId key 로 DeadLock 해소
- **Worker Verticle** — 블로킹 코드(JPA)를 Event Loop에서 격리하는 패턴
- **Bulk Upsert** — 건별 DB 접근 대신 배치로 묶어서 DB flush 병목 해소
- **Kafka + Vert.x** — Consumer를 Verticle 안에서 돌려 Event Loop과 자연스럽게 통합