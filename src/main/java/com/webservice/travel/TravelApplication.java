package com.webservice.travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
 * STEP 5. @EnableJpaAuditing은 JPA Auditing 기능을 켠다.
 * 이 설정이 있어야 Entity의 @CreatedDate, @LastModifiedDate 필드에
 * 등록/수정 시각이 자동으로 채워진다. 앱 전체에 한 번만 선언하면 된다.
 */
@EnableJpaAuditing
@SpringBootApplication
public class TravelApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelApplication.class, args);
    }
}
