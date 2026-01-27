# Getting Started

### 모듈 구조

프로젝트 모듈 구성은 다음과 같습니다.

- api: Spring Boot 애플리케이션 및 웹/API 레이어
- core: 비즈니스 로직 (서비스, 검증, 가격 계산 규칙)
- domain: 도메인 모델 (엔티티, 값 객체, 예외, 리포지토리 인터페이스)
- infra: 인프라 구현 (예: 인메모리 리포지토리)
- global: 공통/횡단 관심사 타입 (예: 검증 오류 DTO)

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.1/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.1/gradle-plugin/packaging-oci-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.1/reference/web/servlet.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans ??insights for your project's build](https://scans.gradle.com#gradle)
