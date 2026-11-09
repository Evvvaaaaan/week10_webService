package com.webservice.travel.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/*
 * Q2. Entity 클래스와 데이터베이스 테이블은 어떻게 연결되는가?
 * - @Entity를 붙이면 JPA가 이 클래스를 관리 대상으로 인식하고, 클래스 이름을 테이블 이름으로 사용한다.
 *   여기서는 @Table(name = "travels")로 테이블 이름을 직접 지정했기 때문에 travels 테이블과 연결된다.
 * - 클래스의 필드 하나가 테이블의 컬럼 하나가 되고, 필드 이름이 기본 컬럼 이름이 된다.
 *   departureDate, durationDays처럼 카멜케이스인 필드는 스프링 부트 기본 네이밍 전략이
 *   departure_date, duration_days로 바꿔 준다. @Column으로 길이, NULL 허용 여부, 정밀도를 정한다.
 * - 즉 Entity는 테이블 한 행을 자바 객체로 옮긴 것이고, JPA가 이 객체와 테이블 사이를 오가며 값을 채운다.
 */
@Entity
@Table(name = "travels")
@EntityListeners(AuditingEntityListener.class)
public class Travel {
    /*
     * Q1. @Entity, @Id, @GeneratedValue는 각각 어떤 역할을 하는가?
     * - @Entity: 이 클래스가 DB 테이블과 매핑되는 JPA Entity임을 알린다. 이 표시가 있어야
     *   JpaRepository가 다루는 대상이 되고, Hibernate가 테이블 구조를 만들거나 조회할 수 있다.
     * - @Id: 이 필드가 테이블의 기본키(PK)임을 표시한다. 기본키는 한 행을 유일하게 구분하는 값이라
     *   findById(), deleteById() 같은 기능이 이 값을 기준으로 동작한다.
     * - @GeneratedValue: 기본키 값을 누가 만들지 정한다. strategy = IDENTITY는 DB의
     *   AUTO_INCREMENT에 맡긴다는 뜻이라, 저장할 때 id를 비워 두면 DB가 값을 채워 준다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false, length = 60)
    private String country;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false)
    private LocalDate departureDate;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    /*
     * STEP 5. Auditing 필드.
     * - @CreatedDate: INSERT 되는 순간의 시각이 자동으로 들어간다. updatable = false로 두어
     *   이후 UPDATE 문에서 이 컬럼이 바뀌지 않게 막았다.
     * - @LastModifiedDate: INSERT와 UPDATE 때마다 그 시점의 시각으로 자동 갱신된다.
     * - 두 필드 모두 코드에서 값을 넣지 않는다. AuditingEntityListener가 값을 채워 준다.
     * - 기존 테이블에 이미 행이 있어 nullable = false로 두면 컬럼 추가 시 실패할 수 있으므로
     *   NULL을 허용했다. 기존 행은 아래 SQL로 한 번 채워 주면 된다.
     *   UPDATE travels SET created_at = NOW(), updated_at = NOW() WHERE created_at IS NULL;
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /*
     * JPA는 DB에서 데이터를 읽어 객체를 만들 때 기본 생성자를 사용한다.
     * 그래서 인자가 없는 생성자가 반드시 필요하고, 외부에서 직접 new Travel()로
     * 빈 객체를 만들지 못하도록 protected로 막아 두었다.
     */
    protected Travel() { }

    public Travel(String destination, String country, String city,
                  LocalDate departureDate, Integer durationDays, BigDecimal budget) {
        this.destination = destination;
        this.country = country;
        this.city = city;
        this.departureDate = departureDate;
        this.durationDays = durationDays;
        this.budget = budget;
    }

    /*
     * setter 대신 의미 있는 이름의 수정 메서드를 두었다. (강의자료 27페이지의 book.update(...) 형태)
     * 이 메서드로 필드 값을 바꾸면 영속성 컨텍스트가 원래 값과 비교해 변경을 감지한다(Dirty Checking).
     */
    public void update(String destination, String country, String city,
                       LocalDate departureDate, Integer durationDays, BigDecimal budget) {
        this.destination = destination;
        this.country = country;
        this.city = city;
        this.departureDate = departureDate;
        this.durationDays = durationDays;
        this.budget = budget;
    }

    public Long getId() { return id; }
    public String getDestination() { return destination; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public LocalDate getDepartureDate() { return departureDate; }
    public Integer getDurationDays() { return durationDays; }
    public BigDecimal getBudget() { return budget; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
