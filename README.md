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
