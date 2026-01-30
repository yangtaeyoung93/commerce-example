# spring-boot

## 모듈 구조

- api: Spring Boot 애플리케이션 및 웹/API 레이어
- core: 비즈니스 로직 (서비스, 검증, 가격 계산 규칙)
- domain: 도메인 모델 (엔티티, 값 객체, 예외, 리포지토리 인터페이스)
- infra: 인프라 구현 (예: 인메모리 리포지토리)
- global: 공통/횡단 관심사 타입 (예: 검증 오류 DTO)

## 빌드

```powershell
./gradlew build
```

## 클린 빌드

```powershell
./gradlew clean build
```
## API 스펙

### 주문 생성

- Endpoint: `POST /api/v1/orders`
- Request (예시)
```json
{
  "memberId": 1,
  "items": [
    { "itemId": 1, "quantity": 10 },
    { "itemId": 2, "quantity": 30 }
  ]
}
```

- Response (예시)
```json
{
  "id": 100,
  "memberId": "1",
  "status": "CREATED",
  "items": [
    { "name": "Group Buy Guide", "quantity": 10, "unitPrice": 15000, "lineTotal": 150000 },
    { "name": "Spring Boot 4.0", "quantity": 30, "unitPrice": 25000, "lineTotal": 750000 }
  ],
  "totalAmount": 900000,
  "createdAt": "2026-01-30T06:00:00Z"
}
```

- Status values: `CREATED`, `PAID`, `SHIPPED`, `CANCELLED`

