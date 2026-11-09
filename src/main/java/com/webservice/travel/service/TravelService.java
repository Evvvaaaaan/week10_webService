package com.webservice.travel.service;

import com.webservice.travel.dto.TravelRequest;
import com.webservice.travel.dto.TravelResponse;
import com.webservice.travel.entity.Travel;
import com.webservice.travel.repository.TravelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

/*
 * @Service는 이 클래스가 비즈니스 로직을 담당하는 스프링 빈임을 알린다.
 * @Transactional을 클래스에 붙이면 이 클래스의 public 메서드가 실행될 때마다 트랜잭션이 시작되고,
 * 정상 종료하면 커밋, 예외가 나면 롤백된다.
 */
@Service
@Transactional
public class TravelService {
    private final TravelRepository repository;

    public TravelService(TravelRepository repository) {
        this.repository = repository;
    }

    /*
     * Q7. save()를 호출하면 새로운 데이터 등록과 기존 데이터 수정이 어떻게 처리되는가?
     * - save()는 넘겨받은 Entity의 id 값을 먼저 확인한다.
     * - id가 null이면 아직 저장된 적이 없는 새 데이터로 판단해 persist()를 호출하고 INSERT를 실행한다.
     * - id가 값으로 채워져 있으면 이미 DB에 있는 데이터로 판단해 merge()를 호출하고 UPDATE를 실행한다.
     *   (id는 @GeneratedValue(IDENTITY)에 의해 INSERT가 끝난 뒤에 채워지므로,
     *    새로 만든 객체는 저장 전까지 id가 null이다.)
     * - create()는 id가 없는 새 Travel을 save()하므로 INSERT가 되고,
     *   update()는 findById()로 찾은 id가 있는 Travel을 save()하므로 UPDATE가 된다.
     */
    public TravelResponse create(TravelRequest request) {
        Travel travel = new Travel(request.destination(), request.country(), request.city(),
                request.departureDate(), request.durationDays(), request.budget());
        return TravelResponse.from(repository.save(travel));
    }

    public List<TravelResponse> findAll() {
        return toResponses(repository.findAll());
    }

    /*
     * STEP 3. Query Method를 호출하는 검색 기능들.
     * Repository가 이름 규칙으로 쿼리를 만들어 주므로 Service는 메서드를 호출해
     * 결과를 DTO로 바꾸는 일만 한다.
     */

    // [단일 조건] 국가가 정확히 일치하는 여행을 조회한다.
    public List<TravelResponse> findByCountry(String country) {
        return toResponses(repository.findByCountry(country));
    }

    // [복합 조건] 국가가 일치하면서 여행일수가 minDays ~ maxDays 사이인 여행을 조회한다.
    public List<TravelResponse> findByCountryAndDurationDaysBetween(String country, Integer minDays, Integer maxDays) {
        return toResponses(repository.findByCountryAndDurationDaysBetween(country, minDays, maxDays));
    }

    // [정렬] 여행지 이름에 keyword가 포함된 여행을 예산 내림차순으로 조회한다. 대소문자는 구분하지 않는다.
    public List<TravelResponse> findByDestinationKeyword(String keyword) {
        return toResponses(repository.findByDestinationContainingIgnoreCaseOrderByBudgetDesc(keyword));
    }

    // [정렬] 예산이 minBudget보다 큰 여행을 출발일 오름차순으로 조회한다.
    public List<TravelResponse> findByMinBudget(BigDecimal minBudget) {
        return toResponses(repository.findByBudgetGreaterThanOrderByDepartureDateAsc(minBudget));
    }

    public TravelResponse findById(Long id) {
        return TravelResponse.from(findTravel(id));
    }

    /*
     * STEP 4. Dirty Checking을 이용한 수정.
     * findTravel()이 findById()로 조회한 Entity는 영속성 컨텍스트가 관리하는 영속 상태다.
     * 이 상태에서 travel.update(...)로 필드만 바꾸면, 메서드가 끝나고 트랜잭션이 커밋될 때
     * Hibernate가 최초 조회 값(스냅샷)과 현재 값을 비교해 달라진 컬럼만 UPDATE 한다.
     * 그래서 repository.save()를 호출하지 않아도 DB에 반영된다.
     * (save()를 호출하면 변경 감지가 아니라 merge()가 실행되어 불필요한 동작이 한 번 더 생긴다.)
     */
    public TravelResponse update(Long id, TravelRequest request) {
        Travel travel = findTravel(id);
        travel.update(request.destination(), request.country(), request.city(),
                request.departureDate(), request.durationDays(), request.budget());
        return TravelResponse.from(travel);
    }

    public void delete(Long id) {
        findTravel(id);
        repository.deleteById(id);
    }

    /*
     * Q8. @Transactional과 Dirty Checking은 어떤 관계가 있는가?
     * - 영속성 컨텍스트는 조회한 Entity를 보관하는 공간으로, 세 가지 기능을 한다.
     *   (1) 1차 캐시: 같은 id를 다시 조회하면 DB에 가지 않고 보관 중인 Entity를 돌려준다.
     *   (2) Dirty Checking: 보관 중인 Entity의 변경을 감지해 UPDATE를 실행한다.
     *   (3) 쓰기 지연: INSERT/UPDATE SQL을 모아 두었다가 flush() 시점에 한꺼번에 보낸다.
     * - Dirty Checking은 조회 시점의 값을 스냅샷으로 복사해 두었다가, 커밋 직전 flush 때
     *   현재 값과 비교해 달라진 컬럼만 UPDATE 한다.
     * - 이 비교는 영속성 컨텍스트가 살아 있는 동안에만 가능하다. @Transactional이 메서드 범위에서
     *   트랜잭션과 영속성 컨텍스트를 유지해 주기 때문에 Dirty Checking이 동작한다.
     * - 반대로 트랜잭션 밖에서 조회한 Entity는 준영속(detached) 상태가 되어 변경을 감지하지 못하고,
     *   값을 바꿔도 DB에 반영되지 않는다.
     * - 그래서 @Transactional이 없으면 save()를 다시 호출해야 반영되고,
     *   @Transactional이 있으면 save() 없이도 커밋 시점에 자동으로 UPDATE 된다.
     */
    private Travel findTravel(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Travel not found"));
    }

    // Entity 목록을 응답 DTO 목록으로 바꾸는 공통 처리. 검색 메서드마다 반복하지 않도록 분리했다.
    private List<TravelResponse> toResponses(List<Travel> travels) {
        return travels.stream().map(TravelResponse::from).toList();
    }
}
