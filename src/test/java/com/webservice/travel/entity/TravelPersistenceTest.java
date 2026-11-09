package com.webservice.travel.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TravelPersistenceTest {
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesAndReloadsTravelWithGeneratedId() {
        Travel travel = new Travel("Jeju Island", "South Korea", "Jeju",
                LocalDate.of(2027, 1, 10), 3, new BigDecimal("350000.00"));

        entityManager.persistAndFlush(travel);
        Long id = travel.getId();
        entityManager.clear();

        assertThat(id).isNotNull();
        Travel saved = entityManager.find(Travel.class, id);
        assertThat(saved.getDestination()).isEqualTo("Jeju Island");
        assertThat(saved.getCountry()).isEqualTo("South Korea");
        assertThat(saved.getCity()).isEqualTo("Jeju");
        assertThat(saved.getDepartureDate()).isEqualTo(LocalDate.of(2027, 1, 10));
        assertThat(saved.getDurationDays()).isEqualTo(3);
        assertThat(saved.getBudget()).isEqualByComparingTo("350000.00");
    }
}
