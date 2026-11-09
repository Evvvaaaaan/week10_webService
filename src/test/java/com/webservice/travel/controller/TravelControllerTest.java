package com.webservice.travel.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.webservice.travel.dto.TravelRequest;
import com.webservice.travel.service.TravelService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/*
 * STEP 3 검색 API가 강의자료 14페이지 형태(GET /api/travels?keyword=...)로
 * 올바른 메서드에 연결되는지 확인한다.
 * 같은 경로에 검색 API가 여러 개 붙어 있어서, 파라미터 조합에 따라
 * 원하는 메서드가 선택되는지를 검증하는 것이 목적이다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TravelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TravelService service;

    @BeforeEach
    void setUp() {
        service.create(new TravelRequest("Seoul tour", "South Korea", "Seoul",
                LocalDate.of(2027, 1, 5), 2, new BigDecimal("200000")));
        service.create(new TravelRequest("Osaka tour", "Japan", "Osaka",
                LocalDate.of(2027, 3, 1), 4, new BigDecimal("900000")));
        service.create(new TravelRequest("Tokyo tour", "Japan", "Tokyo",
                LocalDate.of(2027, 2, 1), 6, new BigDecimal("1200000")));
    }

    @Test
    void findAllWhenNoParameter() throws Exception {
        mockMvc.perform(get("/api/travels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void searchByKeywordSortsByBudgetDesc() throws Exception {
        mockMvc.perform(get("/api/travels").param("keyword", "TOUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].destination").value("Tokyo tour"))
                .andExpect(jsonPath("$[2].destination").value("Seoul tour"));
    }

    @Test
    void searchByCountry() throws Exception {
        mockMvc.perform(get("/api/travels").param("country", "Japan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void searchByCountryAndDurationUsesMostSpecificMapping() throws Exception {
        mockMvc.perform(get("/api/travels")
                        .param("country", "Japan")
                        .param("minDays", "5")
                        .param("maxDays", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destination").value("Tokyo tour"));
    }

    @Test
    void searchByMinBudgetSortsByDepartureDateAsc() throws Exception {
        mockMvc.perform(get("/api/travels").param("minBudget", "500000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].destination").value("Tokyo tour"))
                .andExpect(jsonPath("$[1].destination").value("Osaka tour"));
    }
}
