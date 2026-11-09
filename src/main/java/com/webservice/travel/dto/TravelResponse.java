package com.webservice.travel.dto;

import com.webservice.travel.entity.Travel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * 응답 DTO. Entity를 그대로 돌려주지 않고 필요한 값만 골라 담는다.
 * STEP 5에서 추가된 createdAt, updatedAt도 함께 내려 주어 Auditing 결과를 API로 확인할 수 있게 했다.
 */
public record TravelResponse(
        Long id,
        String destination,
        String country,
        String city,
        LocalDate departureDate,
        Integer durationDays,
        BigDecimal budget,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TravelResponse from(Travel travel) {
        return new TravelResponse(travel.getId(), travel.getDestination(), travel.getCountry(),
                travel.getCity(), travel.getDepartureDate(), travel.getDurationDays(), travel.getBudget(),
                travel.getCreatedAt(), travel.getUpdatedAt());
    }
}
