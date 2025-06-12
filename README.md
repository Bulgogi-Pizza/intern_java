# Spring Boot JWT 인증/인가 프로젝트

## 📖 프로젝트 개요
이 프로젝트는 Spring Boot 프레임워크를 사용하여 JWT(Json Web Token) 기반의 사용자 인증 및 인가 시스템을 구현하는 과제입니다. 사용자는 회원가입과 로그인을 할 수 있으며, 발급된 JWT를 통해 보호된 API에 접근할 수 있습니다. 또한, 역할(Role) 기반의 접근 제어를 적용하여 관리자(Admin)만 접근할 수 있는 API를 구현했습니다.

모든 기능은 Junit5를 통해 테스트되었으며, API 명세는 Swagger(OpenAPI)를 통해 문서화되었습니다.

## ✨ 주요 기능
- **사용자 인증**
    - 사용자 회원가입
    - 사용자 로그인 및 JWT 발급
- **JWT 기반 인가**
    - JWT 토큰 검증 필터를 통한 API 접근 제어
    - 토큰 유효성 검사 (서명, 만료 시간)
    - 토큰 정보에 기반한 사용자 인증 객체 생성
- **역할 기반 접근 제어 (RBAC)**
    - `USER`와 `ADMIN` 역할(Role) 구분
    - `@Secured` 어노테이션을 이용한 관리자 API 접근 제한
- **예외 처리**
    - `@RestControllerAdvice`를 이용한 전역 예외 처리로 일관된 에러 응답 형식 제공
- **API 문서화**
    - `Springdoc-openapi` (Swagger)를 이용한 API 명세 자동화

## ⚙️ 사용 기술
- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Web
- **Security**: JJWT (JSON Web Token 라이브러리)
- **Database**: In-Memory (H2, JPA 등 실제 DB 미사용)
- **Testing**: JUnit 5, Spring Boot Test, MockMvc
- **API Docs**: Springdoc OpenAPI (Swagger UI)
- **Build Tool**: Gradle
- **Utilities**: Lombok

## 🚀 API 명세

- **Swagger UI 주소**: `http://13.203.103.119:8080/swagger-ui.html`
- **API Base URL**: `http://13.203.103.119:8080`

---

### 👤 User API

#### 1. 회원가입
- **Endpoint**: `POST /signup`
- **Description**: 새로운 사용자를 시스템에 등록합니다.
- **Request Body**:
  ```
  {
    "username": "newuser",
    "password": "password1234",
    "nickname": "mynickname"
  }
  ```
  
- **Success Response (200 OK)**:
  ```
  {
    "username": "newuser",
    "nickname": "mynickname",
    "roles": [
      {
        "role": "USER"
      }
    ]
  }
  ```

- **Failure Response (409 Conflict)**:
  ```
  {
    "error": {
      "code": "USER_ALREADY_EXISTS",
      "message": "이미 가입된 사용자입니다."
      }
  }


#### 2. 로그인
- **Endpoint**: `POST /login`
- **Description**: 사용자 인증 후 JWT를 발급합니다.
- **Request Body**:
  ```
  {
    "username": "newuser",
    "password": "password1234"
  }
  ```

- **Success Response (200 OK)**:
  ```
  {
    "token": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi..."
  }
  ```

- **Failure Response (401 Unauthorized)**:
  ```
  {
    "error": {
      "code": "INVALID_CREDENTIALS",
      "message": "아이디 또는 비밀번호가 올바르지 않습니다."
      }
  }
  ```


---

### 🛠️ Admin API
> **[필독]** 아래 API는 **ADMIN** 역할의 사용자만 호출할 수 있습니다. Swagger UI에서 테스트 시, 먼저 로그인하여 얻은 **관리자 JWT**를 우측 상단 `Authorize` 버튼에 등록해야 합니다.

#### 1. 관리자 권한 부여
- **Endpoint**: `PATCH /admin/users/{userId}/roles`
- **Description**: 특정 사용자에게 관리자(ADMIN) 권한을 부여합니다.
- **Path Variable**:
- `userId` (Long): 권한을 부여할 사용자의 ID
- **Success Response (200 OK)**:
  ```
  {
  "username": "someuser",
  "nickname": "somenickname",
  "roles": [
      {
        "role": "ADMIN"
      }
    ]
  }
  ```

- **Failure Responses**:
- **403 Forbidden** (접근 권한 없음):
  ```
  {
    "error": {
      "code": "ACCESS_DENIED",
      "message": "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."
    }
  }
  ```
- **400 Bad Request** (존재하지 않는 사용자):
  ```
  {
    "error": {
      "code": "INVALID_ARGUMENT",
      "message": "해당 ID의 사용자를 찾을 수 없습니다."
    }
  }
  ```

## ❗ 주요 에러 코드
| Error Code             | HTTP Status     | Description                            |
| ---------------------- | --------------- | -------------------------------------- |
| `USER_ALREADY_EXISTS`  | 409 Conflict    | 회원가입 시 이미 존재하는 사용자 이름일 경우 |
| `INVALID_CREDENTIALS`  | 401 Unauthorized| 로그인 시 아이디 또는 비밀번호가 틀렸을 경우 |
| `INVALID_TOKEN`        | 401 Unauthorized| JWT 토큰이 유효하지 않거나 만료되었을 경우   |
| `ACCESS_DENIED`        | 403 Forbidden   | 해당 API에 접근할 권한이 없는 경우         |
| `INVALID_ARGUMENT`     | 400 Bad Request | 요청 파라미터가 유효하지 않은 경우 (예: 존재하지 않는 userId) |


## 🏃‍ 실행 방법

### 1. 사전 요구사항
- Java 17 (또는 그 이상)
- Gradle 7.x (또는 그 이상)

### 2. 빌드
프로젝트 루트 디렉토리에서 아래 명령어를 실행하여 프로젝트를 빌드합니다.
./gradlew build


### 3. 실행
빌드가 완료되면 `build/libs` 경로에 실행 가능한 `.jar` 파일이 생성됩니다. 아래 명령어로 애플리케이션을 실행합니다.
> 파일 이름의 버전은 프로젝트 설정에 따라 다를 수 있습니다.

java -jar build/libs/intern_java-0.0.1-SNAPSHOT.jar

애플리케이션은 기본적으로 `8080` 포트에서 실행됩니다.
