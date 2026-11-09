package com.webservice.travel.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.webservice.travel.domain.dto.TravelRequest;
import com.webservice.travel.domain.dto.TravelResponse;
import com.webservice.travel.domain.model.Travel;
import com.webservice.travel.domain.repository.TravelRepository;
import com.webservice.travel.global.exception.CustomException;
import com.webservice.travel.global.exception.constant.ExceptionCode;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/*
 * STEP 3~5 동작 확인용 테스트.
 * H2 메모리 DB로 실행되므로 MariaDB 없이도 검색, Dirty Checking, Auditing을 검증할 수 있다.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TravelServiceTest {
    @Autowired
    private TravelService service;

    @Autowired
    private TravelRepository repository;

    @Autowired
    private EntityManager entityManager;

    private TravelRequest request(String destination, String country, String city,
                                  LocalDate departureDate, int durationDays, String budget) {
        return new TravelRequest(destination, country, city, departureDate, durationDays, new BigDecimal(budget));
    }

    @Test
    void auditingFillsCreatedAndUpdatedAtOnCreate() {
        TravelResponse created = service.create(
                request("Busan Beach", "South Korea", "Busan", LocalDate.of(2027, 2, 10), 2, "200000"));

        assertThat(created.createdAt()).isNotNull();
        assertThat(created.updatedAt()).isNotNull();
    }

    @Test
    void updateIsAppliedByDirtyCheckingWithoutSave() {
        TravelResponse created = service.create(
                request("Jeju Island", "South Korea", "Jeju", LocalDate.of(2027, 1, 10), 3, "350000"));

        // save() 없이 값만 바꾼다. 커밋 시점에 Dirty Checking으로 UPDATE 되어야 한다.
        service.update(created.id(),
                request("Jeju Island", "South Korea", "Jeju", LocalDate.of(2027, 1, 10), 5, "500000"));

        entityManager.flush();
        entityManager.clear();

        Travel reloaded = repository.findById(created.id()).orElseThrow();
        assertThat(reloaded.getDurationDays()).isEqualTo(5);
        assertThat(reloaded.getBudget()).isEqualByComparingTo("500000");
        assertThat(reloaded.getUpdatedAt()).isNotNull();
    }

    @Test
    void queryMethodsFilterAndSort() {
        service.create(request("Seoul tour", "South Korea", "Seoul", LocalDate.of(2027, 1, 5), 2, "200000"));
        service.create(request("Osaka tour", "Japan", "Osaka", LocalDate.of(2027, 3, 1), 4, "900000"));
        service.create(request("Tokyo tour", "Japan", "Tokyo", LocalDate.of(2027, 2, 1), 6, "1200000"));
        entityManager.flush();
        entityManager.clear();

        // 단일 조건: 국가 일치
        List<TravelResponse> korea = service.findByCountry("South Korea");
        assertThat(korea).hasSize(1);
        assertThat(korea.get(0).destination()).isEqualTo("Seoul tour");

        // 복합 조건: 국가 일치 And 여행일수 Between
        List<TravelResponse> japanShort = service.findByCountryAndDurationDaysBetween("Japan", 1, 4);
        assertThat(japanShort).hasSize(1);
        assertThat(japanShort.get(0).destination()).isEqualTo("Osaka tour");

        // 정렬: 여행지 부분 일치 + 예산 내림차순
        List<TravelResponse> sortedByBudget = service.findByDestinationKeyword("tour");
        assertThat(sortedByBudget).extracting(TravelResponse::destination)
                .containsExactly("Tokyo tour", "Osaka tour", "Seoul tour");

        // 정렬: 예산 초과 + 출발일 오름차순
        List<TravelResponse> expensiveFirst = service.findByMinBudget(new BigDecimal("500000"));
        assertThat(expensiveFirst).extracting(TravelResponse::destination)
                .containsExactly("Tokyo tour", "Osaka tour");
    }

    @Test
    void createWithMissingFieldThrowsInvalidRequest() {
        TravelRequest incomplete = new TravelRequest("Busan Beach", "South Korea", "Busan",
                LocalDate.of(2027, 2, 10), 2, null);

        assertThatThrownBy(() -> service.create(incomplete))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.INVALID_REQUEST);
    }

    @Test
    void findByIdWithUnknownIdThrowsIdNotFound() {
        assertThatThrownBy(() -> service.findById(9999L))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.ID_NOT_FOUND);
    }
}
