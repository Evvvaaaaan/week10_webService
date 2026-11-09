# 프로젝트 소개

## 주제

- 주제는 여행 계획. 등록, 조회, 수정, 삭제하고 MariaDB에 저장함.
- 9주차 CRUD 프로젝트를 기반으로 Query Method 검색·정렬, Dirty Checking 수정, Auditing 시각 자동 기록을 추가했다.
- 필수 값 없으면 400, 없는 id면 404

## Travel 필드

- 테이블 travels / Entity는 Travel 하나
- id: Long, 기본키, AUTO_INCREMENT
- destination: String, 여행지, VARCHAR(100), NOT NULL
- country: String, 국가, VARCHAR(60), NOT NULL
- city: String, 도시, VARCHAR(100), NOT NULL
- departureDate: LocalDate, 출발일, departure_date 컬럼, NOT NULL
- durationDays: Integer, 여행일수, duration_days 컬럼, NOT NULL
- budget: BigDecimal, 예산, DECIMAL(12,2), NOT NULL
- createdAt: LocalDateTime, 생성 시각, created_at 컬럼, Auditing으로 자동 기록
- updatedAt: LocalDateTime, 수정 시각, updated_at 컬럼, Auditing으로 자동 기록

## 구조, 사용한 것

- domain/controller: TravelController, REST API
- domain/service: TravelService, 비즈니스 로직과 요청 검증
- domain/repository: TravelRepository, JpaRepository와 Query Method
- domain/model: Travel Entity
- domain/dto: TravelRequest, TravelResponse
- global/exception: CustomException, ExceptionCode, ExceptionResponse, CustomizedResponseEntityExceptionHandler
- Controller에서 Service 호출, Repository 거쳐 Hibernate가 MariaDB에 SQL 실행
- 개발환경은 Java 17, Spring Boot 3.5.6, Gradle 8.14, Spring Data JPA, MariaDB, IntelliJ, Postman, DBeaver, Docker, Render다.

## GitHub Repositoryy URL

- Organization: https://github.com/2026-2-WebService/assign10-c01-22300404
- 개인 저장소: https://github.com/Evvvaaaaan/week10_webService

## Render Deployment URL

- https://week10-webservice-184j.onrender.com

# 2 Database SQL Practice

## 기존 테이블

- INSERT INTO book (title, author) VALUES ('Database Practice', 'Student');
- 같은 INSERT를 두 번 실행해 id 1, 2에 같은 내용이 2행 저장됐다.
- SELECT * FROM book; 실행 결과 2행이 조회됐다.
- 화면: 기존 book 테이블 조회와 데이터 추가 / screenshots/sql_existing.png

## 새 테이블 만들기

- SQL 실습은 movie_sql, JPA 테이블은 travels. 이름 다르게 사용
- CREATE TABLE movie_sql (id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(100) NOT NULL, director VARCHAR(100), genre VARCHAR(50), rating DOUBLE);
- 화면: movie_sql 생성 / screenshots/sql_create.png

## INSERT / UPDATE / DELEETE / SELECT SQL과 실행 결과

- INSERT: INSERT INTO movie_sql (title, director, genre, rating) VALUES ('Inception', 'Nolan', 'SF', 9.1), ('Interstellar', 'Nolan', 'SF', 9.3), ('Parasite', 'Bong', 'Drama', 9.0);
- INSERT 결과 3행이 저장됐다. 추가 실습에서 id 4, 5, 6으로 들어간 직후 화면을 찍음.
- 화면: INSERT 후 추가한 3행 조회 / screenshots/sql_insert.png
- 촬영 후 추가 실습 행 3개만 삭제하고 기존 2행은 유지함.
- UPDATE: UPDATE movie_sql SET rating = 9.5 WHERE title = 'Interstellar';
- UPDATE 결과 Interstellar의 rating이 9.3에서 9.5로 바뀌었고, 조회하면 3행 중 해당 행만 변경되어 있다.
- 화면: UPDATE 실행 결과 / screenshots/sql_update.png

- DELETE: DELETE FROM movie_sql WHERE title = 'Parasite';
- Parasite 삭제하고 다시 보면 2행 남음
- 화면: DELETE 실행 결과 / screenshots/sql_delete.png

- SELECT 전체 조회: SELECT * FROM movie_sql; 실행 결과는 위 DELETE 화면에서 2행으로 확인할 수 있다.
- SELECT 컬럼 지정: SELECT title, rating FROM movie_sql; 실행 결과 title과 rating 두 컬럼만 2행 조회됐다.
- 화면: 컬럼 지정 조회 / screenshots/sql_select_columns.png

- SELECT 조건 지정: SELECT * FROM movie_sql WHERE genre = 'SF'; 실행 결과 SF 영화 2행만 조회됐다.
- 화면: 조건 지정 조회 / screenshots/sql_select_where.png

## DBeaver 확인

- 화면 파일은 DBeaver에서 실행한 결과
- UPDATE, DELETE 하고 SELECT로 다시 확인함.

# 3 Spring Data JPA 구현

## Entity, Repository

- TravelRepository extends JpaRepository<Travel, Long>
- save, findAll, findById, deleteById는 따로 구현하지 않고 사용

## Annotation 정리

- @Entity: 클래스를 JPA가 관리하는 Entity로 지정한다.
- @Table: Entity와 매핑할 테이블 이름을 지정한다.
- @Id: 기본키 필드를 지정한다.
- @GeneratedValue: 기본키 생성 방식을 정하고 IDENTITY는 DB의 AUTO_INCREMENT를 사용한다.
- @Column: 컬럼의 길이, NULL 허용 여부, 정밀도를 지정한다.
- @EntityListeners(AuditingEntityListener.class): 저장·수정 시점에 Auditing 리스너가 동작하게 한다.
- @CreatedDate: 처음 저장될 때 시각을 자동 기록한다.
- @LastModifiedDate: 저장·수정될 때 시각을 자동 갱신한다.
- @EnableJpaAuditing: JPA Auditing 기능을 켠다.
- @Transactional: 메서드의 DB 작업을 하나의 트랜잭션으로 묶는다.
- @RestController, @RequestMapping: JSON 응답 컨트롤러와 공통 경로를 지정한다.
- @RequestBody, @PathVariable, @RequestParam: 요청 본문, 경로 값, 쿼리 파라미터를 받는다.
- @RestControllerAdvice, @ExceptionHandler: 예외를 공통 JSON 응답으로 바꾼다.

## REST API Endpoint 목록

- POST /api/travels: 등록, 201과 생성된 TravelResponse
- GET /api/travels: 전체 조회, 200과 배열
- GET /api/travels/{id}: 단건 조회, 200
- PUT /api/travels/{id}: 수정, 200
- DELETE /api/travels/{id}: 삭제, 204
- GET /api/travels?country=Japan: 국가 검색
- GET /api/travels?country=Japan&minDays=5&maxDays=7: 국가와 여행일수 범위 검색
- GET /api/travels?keyword=tour: 여행ㅇ지 부분 검색, 예산 내림차순
- GET /api/travels?minBudget=500000: 예산 초과 검색, 출발일 오름차순

## 요청·응답 예시

- 아래 JSON은 API 형식을 설명하는 예시이며 실제 실행 기록은 아니다. id와 시각은 실행에 따라 달라지고, 전체 조회 예시는 데이터가 1건인 경우다.
- POST /api/travels 등록
- 요청 JSON: {"destination":"Seoul tour","country":"South Korea","city":"Seoul","departureDate":"2027-05-01","durationDays":3,"budget":400000}
- 응답 JSON, 201: {"id":1,"destination":"Seoul tour","country":"South Korea","city":"Seoul","departureDate":"2027-05-01","durationDays":3,"budget":400000,"createdAt":"2026-11-09T10:00:00","updatedAt":"2026-11-09T10:00:00"}

- GET /api/travels 전체 조회: 요청 본문 없음.
- 응답 JSON, 200: [{"id":1,"destination":"Seoul tour","country":"South Korea","city":"Seoul","departureDate":"2027-05-01","durationDays":3,"budget":400000,"createdAt":"2026-11-09T10:00:00","updatedAt":"2026-11-09T10:00:00"}]

- GET /api/travels/1 단건 조회: 요청 본문 없음.
- 응답 JSON, 200: {"id":1,"destination":"Seoul tour","country":"South Korea","city":"Seoul","departureDate":"2027-05-01","durationDays":3,"budget":400000,"createdAt":"2026-11-09T10:00:00","updatedAt":"2026-11-09T10:00:00"}

- PUT /api/travels/1 수정
- 요청 JSON: {"destination":"Seoul tour","country":"South Korea","city":"Seoul","departureDate":"2027-05-01","durationDays":7,"budget":800000}
- 응답 JSON, 200: {"id":1,"destination":"Seoul tour","country":"South Korea","city":"Seoul","departureDate":"2027-05-01","durationDays":7,"budget":800000,"createdAt":"2026-11-09T10:00:00","updatedAt":"2026-11-09T10:00:00"}
- 현재 구현은 트랜잭션 커밋 전에 응답 DTO를 만들므로 PUT 응답의 updatedAt은 수정 전 시각일 수 있다.

- GET /api/travels/1 수정 결과 조회: 요청 본문 없음.
- 응답 JSON, 200: {"id":1,"destination":"Seoul tour","country":"South Korea","city":"Seoul","departureDate":"2027-05-01","durationDays":7,"budget":800000,"createdAt":"2026-11-09T10:00:00","updatedAt":"2026-11-09T10:05:00"}

- DELETE /api/travels/1 삭제: 요청 본문 없음.
- 응답: 204 No Content. 응답 본문이 없으므로 JSON도 반환하지 않는다.

## Entity랑 DB 컬럼

- id는 id 컬럼, bigint, 기본키, auto_increment로 만들어진다.
- destination은 destination 컬럼, varchar(100) not null이다.
- country는 country 컬럼, varchar(60) not null이다.
- city는 city 컬럼, varchar(100) not null이다.
- departureDate는 departure_date 컬럼, date not null이다.
- durationDays는 duration_days 컬럼, integer not null이다.
- budget은 budget 컬럼, decimal(12,2) not null이다.
- createdAt은 created_at 컬럼, datetime(6)이다.
- updatedAt은 updated_at 컬럼, datetime(6)이다.
- 카멜케이스 필드는 스프링 부트 기본 네이밍 전략으로 스네이크케이스 컬럼이 된다.

# 4 JPA와 SQL 비교

## save(), findAll(), findById(), deleteById() 사용 결과

- save(): 이 프로젝트에서는 새 Travel 등록에 사용하며 INSERT가 실행된다. 수정은 @Transactional 안에서 조회한 Entity의 값을 바꾸는 Dirty Checking으로 처리하므로 save()를 호출하지 않는다.
- findAll(): 전체 조회에 사용한다. SELECT 결과를 List로 받는다.
- findById(): 단건 조회와 수정 전 조회에 사용한다. Optional을 반환한다.
- deleteById(): 삭제에 사용한다. 삭제 전에 findById()로 존재를 확인해 없는 id면 404를 응답한다.

## 콘솔에 나온 SQL

- spring.jpa.show-sql=true로 콘솔에서 SQL을 확인했다.
- 등록: insert into travels (budget, city, country, created_at, departure_date, destination, duration_days, updated_at, id) values (?, ?, ?, ?, ?, ?, ?, ?, default)
- 전체 조회: select t1_0.id, t1_0.budget, t1_0.city, t1_0.country, t1_0.created_at, t1_0.departure_date, t1_0.destination, t1_0.duration_days, t1_0.updated_at from travels t1_0
- 단건 조회: select t1_0.id, t1_0.budget, t1_0.city, t1_0.country, t1_0.created_at, t1_0.departure_date, t1_0.destination, t1_0.duration_days, t1_0.updated_at from travels t1_0 where t1_0.id=?
- 수정: update travels set budget=?, city=?, country=?, departure_date=?, destination=?, duration_days=?, updated_at=? where id=?
- 삭제: delete from travels where id=?
- 수정 UPDATE에 created_at이 빠진 것은 @Column(updatable = false) 때문이다.

## SQL 직접 쓰는 것과 JPA 차이

- SQL 직접 작성은 테이블과 INSERT, SELECT, UPDATE, DELETE를 직접 쓴다.
- JPA는 Entity와 메서드 호출로 처리하고 SQL은 Hibernate가 만든다.
- 테이블 생성도 ddl-auto=update 설정으로 Entity 기준 자동 생성된다.
- SQL 직접 작성은 실행 쿼리를 정확히 제어할 수 있지만 반복 코드가 많다.
- JPA는 반복이 줄고 객체 중심으로 개발할 수 있지만 실제 실행 SQL을 로그로 확인해야 한다.

# 5 배포 및 환경 설정

## IntelliJ 및 Render 환경변수 설정 방법

- 실제 값은 적지 않고 변수명만 기록
- DB_URL: JDBC 접속 주소
- DB_USERNAME: DB 계정
- DB_PASSWORD: DB 비밀번호
- IntelliJ는 Run > Edit Configurations > Environment variables에 등록한다.
- Render는 서비스의 Environment 탭에 등록한다.
- PORT는 Render가 자동으로 넣고 application.properties의 server.port=${PORT:8080}이 읽는다.

## Render 배포 과정

- GitHub에 push한 저장소를 Render Web Service로 연결했다.
- Runtime은 Docker, Dockerfile은 프로젝트 루트의 것을 사용했다.
- Dockerfile은 gradle:8.14-jdk17에서 test와 bootJar를 실행하고 eclipse-temurin:17-jre에서 jar를 실행한다.
- Health Check Path는 /api/travels로 지ㅈ정했다.
- DB_URL, DB_USERNAME, DB_PASSWORD를 Environment에 넣고 배포했다.

## 배포 URL의 CRUD 테스트 결과

- GET /api/travels: 200, 기존 데이터 조회
- POST /api/travels: 201, id 8 생성
- GET /api/travels/8: 200, 생성 데이터 조회
- PUT /api/travels/8: 200, durationDays 3에서 7, budget 400000에서 800000으로 수정
- GET /api/travels/8: 200, 수정된 값과 바뀐 updatedAt 확인
- DELETE /api/travels/8: 204
- GET /api/travels/8: 404 ID_NOT_FOUND
- GET /api/travels?country=South Korea: 200, 3건
- GET /api/travels?keyword=deploy: 200, 1건
- GET /api/travels?country=South Korea&minDays=5&maxDays=10: 200, 1건
- GET /api/travels?minBudget=500000: 200, 3건
- 필수 값이 빠진 POST: 400 INVALID_REQUEST
- 테스트한 id 8은 삭제함. 기존 데이터만 남겨둠.

## MariaDB 데이터 저장 확인

- Render 주소에서 POST /api/travels 호출, 201 응답과 id 10 확인.
- 여행지 Render DB Evidence 20261109, 예산 400000으로 등록됨.
- DBeaver 원격 MariaDB에서 id 10과 같은 여행지 이름으로 SELECT, 1행 확인함.
- 화면: Render에서 등록한 데이터의 원격 DB 조회 / screenshots/render_remote_insert.png
- 실제 등록 요청과 응답 기록: screenshots/render_remote_post.json
- id 10은 제출 확인용으로 남겨둠. 아래 두 화면은 이전 로컬 DB 실습 결과임.

- 등록하면 created_at, updated_at이 같은 시각
- 화면: 등록 후 DB / screenshots/step10_dbeaver_before.png

- 수정 후 created_at은 그대로이고 updated_at만 바뀌었다. 수정하지 않은 행은 두 값이 그대로다.
- 화면: 수정 후 DB / screenshots/step10_dbeaver_after.png

## 문제 있었던 부분

- Unable to determine Dialect without JDBC metadata 오류가 났다. DB_URL 환경변수가 비어 접속 주소가 없었고, ${DB_URL:기본값} 형식으로 기본값을 넣고 Docker MariaDB를 실행해 해결했다.
- 기존 테이블에 created_at을 NOT NULL로 추가하면 기존 행 때문에 실패할 수 있어 컬럼은 NULL을 허용하고 기존 행은 UPDATE로 채웠다.

# 6 Weekly Report

## Key Learning

- Query Method는 findBy + 필드 + 조건 규칙으로 이름만 지으면 JPQL과 SQL이 만들어져 단순 조건 검색에 SQL을 직접 쓰지 않아도 된다.
- Dirty Checking은 영속성 컨텍스트가 조회 시점의 스냅샷과 현재 값을 비교해 변경을 감지하고 UPDATE를 실행하는 방식이고, @Transactional이 영속 상태를 유지해 준다. 현재 Entity에는 @DynamicUpdate가 없어 UPDATE에는 변경하지 않은 필드를 포함한 수정 가능한 컬럼들이 함께 들어간다.
- Auditing은 @EnableJpaAuditing, @EntityListeners, @CreatedDate, @LastModifiedDate가 함께 있어야 동작하고, updatable = false로 생성 시각을 고정한다.

## Problem & Solution

- 문제: 검색 API를 GET /api/travels?keyword= 형태로 만들자 전체 조회와 경로가 겹쳤다.
- 원인: 같은 경로에 GET 매핑이 여러 개라 스프링이 어느 메서드를 호출할지 결정하지 못했다.
- 해결: @GetMapping(params = ...)로 파라미터가 붙은 요청만 검색 메서드로 보내 해결했다.
- 문제: H2로 테스트할 때 대소문자 때문에 검색 결과가 달라졌다.
- 해결: ContainingIgnoreCase를 사용해 DB 종류와 무관하게 같은 결과가 나오도록 했다.

## Code Review

- 가장 중요한 코드는 TravelService의 update()다.
- findTravel(id)로 repository.findById()를 호출해 Entity를 조회한다.
- 조회한 Entity는 영속성 컹텍스트가 관리하는 영속 상태다.
- travel.update(...)로 필드 값만 바꾸고 repository.save()는 호출하지 않는다.
- @Transactional로 유지되는 영속성 컨텍스트에서 Hibernate가 flush 시점에 최초 값과 현재 값을 비교해 변경을 감지하고 UPDATE를 실행한다. 현재 SQL에는 변경된 컬럼만이 아니라 수정 가능한 컬럼들이 함께 포함되며, updatable = false인 created_at은 제외된다.
- 그래서 save() 없이도 수정이 DB에 반영되고, 응답의 updatedAt은 커밋 전 값이라 조회했을 때보다 이전 시각으로 보일 수 있다.

## AI Usage

- 사용 도구는 CodeBuddy Code다. 개념 설명, 코드 구조 정리, 오류 원인 파악에 사용했다.
- 주요 질문은 save() 없이 수정이 반영되는 이유, @CreatedDate가 값을 채우지 못하는 경우, Containing과 IgnoreCase의 차이다.
- AI가 제안한 검색 메서드는 대소문자를 구분해 결과가 달라지는 문제가 있어 IgnoreCase를 직접 적용했다.
- Auditing 필드를 nullable = false로 두면 기존 데이터 때문에 ALTER가 실패할 수 있어 NULL 허용으로 바꿨다.
- 배포 후 curl로 API를 호출하고, DBeaver로 DB 값을 확인해 결과를 검증했다.
- AI 사용률은 40% 정도로 본다. 개념 설명과 오류 해결 위주였고 최종 코드 확인과 실행 검증은 직접 했다.

## Reflection

- Query Method 이름이 길어지면 가독성이 떨어지는 것을 느꼈다. 조건이 많아질 때 @Query나 Querydsl로 분리하는 방법을 공부하고 싶다.
