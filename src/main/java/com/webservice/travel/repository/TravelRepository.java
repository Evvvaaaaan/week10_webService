package com.webservice.travel.repository;

import com.webservice.travel.entity.Travel;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Q3. JpaRepository<Entity, Long>에서 두 타입은 각각 무엇을 의미하는가?
 * - 첫 번째 타입(Travel): 이 Repository가 관리할 Entity 클래스다. 어떤 테이블을 다룰지 정한다.
 * - 두 번째 타입(Long): 그 Entity의 기본키 타입이다. Travel의 id가 Long이므로 Long을 적는다.
 *   이 두 값 덕분에 findById(Long id)처럼 기본키 타입이 정해진 메서드가 자동으로 만들어진다.
 *
 * Q4. JpaRepository에서 기본 제공하는 CRUD 메서드 (선언하지 않아도 바로 사용 가능)
 * - save(Travel): Entity를 저장한다. id가 없으면 INSERT, id가 있으면 UPDATE를 수행한다.
 * - findById(Long): 기본키로 한 건을 조회한다. 반환 타입은 Optional<Travel>이다.
 * - findAll(): 테이블의 전체 행을 조회한다. 반환 타입은 List<Travel>이다.
 * - deleteById(Long): 기본키로 한 건을 삭제한다.
 * - delete(Travel): 전달한 Entity를 삭제한다.
 * - count(): 전체 행 수를 long으로 돌려준다.
 * - existsById(Long): 해당 기본키의 행이 있는지 boolean으로 알려준다.
 * - saveAll(Iterable): 여러 건을 한 번에 저장한다.
 *
 * Q5. findById()의 반환 타입이 Optional인 이유는 무엇인가?
 * - 기본키에 해당하는 행이 DB에 없을 수도 있기 때문이다. 그런 경우 null을 그대로 돌려주면
 *   호출한 쪽이 null 확인을 잊고 사용하다가 NullPointerException이 날 수 있다.
 * - Optional로 감싸면 "값이 없을 수 있다"는 사실이 타입에 드러나고, isPresent(), orElseThrow()처럼
 *   값이 없을 때를 반드시 처리하도록 강제된다. 이 프로젝트의 findTravel()도 orElseThrow()로
 *   404 예외를 던지는 식으로 그 처리를 하고 있다.
 *
 * Q6. JpaRepository 기본 메서드와 Query Method의 차이는 무엇인가?
 * - 기본 메서드는 스프링 데이터 JPA가 미리 구현해 둔 것이라 이름과 반환 타입이 정해져 있고,
 *   기본키 조회나 전체 조회처럼 조건이 단순하다. 조건을 내 마음대로 붙일 수 없다.
 * - Query Method는 findBy + 필드 이름 + 조건 키워드 규칙으로 메서드 이름을 직접 작성하면,
 *   스프링 데이터 JPA가 그 이름을 해석해 JPQL을 만들어 주는 방식이다. 구현 클래스를 만들 필요가 없다.
 *   예: findByCountry(String country) -> WHERE country = ? 조건의 쿼리가 자동 생성된다.
 * - 정리하면 단순 CRUD는 기본 메서드, 필드 조건 검색은 Query Method를 사용한다.
 */
public interface TravelRepository extends JpaRepository<Travel, Long> {

    /*
     * [단일 조건 검색]
     * country 값이 정확히 일치하는 여행만 조회한다.
     * 생성되는 쿼리: SELECT * FROM travels WHERE country = ?
     */
    List<Travel> findByCountry(String country);

    /*
     * [복합 조건 검색 - And + Between]
     * 국가가 일치하고(And), 여행일수가 minDays 이상 maxDays 이하(Between)인 여행을 조회한다.
     * Between은 양 끝 값을 포함한다.
     * 생성되는 쿼리: SELECT * FROM travels WHERE country = ? AND duration_days BETWEEN ? AND ?
     */
    List<Travel> findByCountryAndDurationDaysBetween(String country, Integer minDays, Integer maxDays);

    /*
     * [정렬 포함 검색 - Containing + IgnoreCase + OrderBy Desc]
     * 여행지 이름에 keyword가 포함된 여행을 예산이 큰 순서(내림차순)로 조회한다.
     * Containing은 LIKE '%keyword%'로 변환되고, IgnoreCase가 붙으면 대소문자를 구분하지 않는다.
     * (강의자료 8~9페이지의 Containing / IgnoreCase에 해당)
     * 생성되는 쿼리: SELECT * FROM travels WHERE UPPER(destination) LIKE UPPER(?) ORDER BY budget DESC
     */
    List<Travel> findByDestinationContainingIgnoreCaseOrderByBudgetDesc(String keyword);

    /*
     * [정렬 포함 검색 - GreaterThan + OrderBy Asc]
     * 예산이 minBudget보다 큰(초과) 여행을 출발일이 빠른 순서(오름차순)로 조회한다.
     * GreaterThan은 강의자료 10페이지의 ">" 비교 조건에 해당한다.
     * 생성되는 쿼리: SELECT * FROM travels WHERE budget > ? ORDER BY departure_date ASC
     */
    List<Travel> findByBudgetGreaterThanOrderByDepartureDateAsc(BigDecimal minBudget);
}
