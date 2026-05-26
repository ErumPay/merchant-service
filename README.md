# merchant-service

ErumPay PG 시스템의 가맹점 도메인을 담당하는 Spring Boot 기반 마이크로서비스입니다.

본 서비스는 부트캠프 팀 프로젝트에서 조보름이 담당하여 구현했습니다.

## 담당 범위

merchant-service는 PG 관리자 및 내부 서비스에서 사용하는 가맹점 도메인을 관리합니다.

주요 관리 대상은 다음과 같습니다.

- 가맹점 기본 정보
- 가맹점 상태
- API Key 발급 및 검증
- 정산 정책 조회용 가맹점 정보
- 영수증 표시용 가맹점 정보
- 상태 변경 이력

## 주요 기능

- 가맹점 등록
- 가맹점 단건 조회
- 가맹점 목록 조회
- 가맹점 정보 수정
- 가맹점 상태 변경
- 가맹점 삭제 처리
- 가맹점 상태 변경 이력 저장 및 조회
- API Key 자동 생성
- API Key 재발급
- API Key 검증
- 내부 서비스용 가맹점 검증
- 내부 서비스용 정산 정책 조회
- 내부 서비스용 영수증 가맹점 정보 조회

## 기술 스택

- Java 21
- Spring Boot
- Spring Data JPA
- Gradle
- MySQL
- Docker

## 프로젝트 구조

```text
src/main/java/com/erumpay/merchantservice
├── controller
├── service
├── repository
├── entity
├── dto
├── enums
└── global
```

## 데이터베이스

기본 DB는 `pg_merchant_db`를 사용합니다.

| Table | Description | MVP 사용 여부 |
| --- | --- | --- |
| `pg_merchants` | 가맹점 기본 정보 | 사용 |
| `pg_merchant_status_history` | 가맹점 상태 변경 이력 | 사용 |
| `pg_settlements` | 정산 정보 | 후속 작업 |
| `pg_merchant_audit_log` | 가맹점 감사 로그 | 후속 작업 |

현재 MVP 범위에서는 `pg_merchants`, `pg_merchant_status_history`를 중심으로 사용합니다.

## 환경변수

서비스 실행 시 다음 환경변수가 필요합니다.

```env
DB_URL=jdbc:mysql://merchant-db:3306/pg_merchant_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
MYSQL_ROOT_PASSWORD=your_root_password
```

실제 환경값은 `.env`에 작성합니다.

`.env` 파일은 Git에 커밋하지 않습니다.

## Docker 실행

이미지를 빌드합니다.

```bash
docker build -t merchant-service:local .
```

컨테이너를 실행합니다.
```bash
docker run --name merchant-service \
  --network erumpay-infra_erumpay-network \
  -p 8094:8094 \
  -e DB_URL=jdbc:mysql://mysql:3306/pg_merchant_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=1234 \
  merchant-service:local
```

기존 컨테이너가 남아 있으면 삭제합니다.
```bash
docker rm -f merchant-service
```

로그 확인:

```bash
docker compose logs -f merchant-service
```

종료:

```bash
docker compose down
```

DB 볼륨까지 삭제:

```bash
docker compose down -v
```

## API 요약

### 관리자 API

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/v1/pg-admin/merchants` | 가맹점 등록 |
| GET | `/api/v1/pg-admin/merchants` | 가맹점 목록 조회 |
| GET | `/api/v1/pg-admin/merchants/{merchantId}` | 가맹점 단건 조회 |
| PUT | `/api/v1/pg-admin/merchants/{merchantId}` | 가맹점 정보 수정 |
| PATCH | `/api/v1/pg-admin/merchants/{merchantId}/status` | 가맹점 상태 변경 |
| DELETE | `/api/v1/pg-admin/merchants/{merchantId}` | 가맹점 삭제 |
| GET | `/api/v1/pg-admin/merchants/{merchantId}/status-history` | 가맹점 상태 이력 조회 |
| PATCH | `/api/v1/pg-admin/merchants/{merchantId}/api-key/rotate` | API Key 재발급 |

### 내부 API

| Method | Path | Description |
| --- | --- | --- |
| GET | `/internal/v1/merchants/{merchantId}` | 내부 가맹점 조회 |
| GET | `/internal/v1/merchants/{merchantId}/validate` | 가맹점 유효성 검증 |
| POST | `/internal/v1/merchants/api-key/validate` | API Key 검증 |
| GET | `/internal/v1/merchants/{merchantId}/settlement-policy` | 정산 정책 조회 |
| GET | `/internal/v1/merchants/{merchantId}/receipt-info` | 영수증용 가맹점 정보 조회 |

## 담당자

- merchant-service 담당: 조보름

## 주의 사항

- `.env`는 Git에 커밋하지 않습니다.
- 실제 DB 계정, 비밀번호, 운영 주소는 README에 작성하지 않습니다.
- 내부 API 인증/인가는 pg-auth 연동 이후 적용 예정입니다.
- Kafka 등 서비스 간 통신은 후속 작업에서 진행 예정입니다.
