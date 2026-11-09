package com.webservice.travel.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TravelRequest(
        String destination,
        String country,
        String city,
        LocalDate departureDate,
        Integer durationDays,
        BigDecimal budget
) {
}
