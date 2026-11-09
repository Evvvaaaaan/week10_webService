package com.webservice.travel.controller;

import com.webservice.travel.dto.TravelRequest;
import com.webservice.travel.dto.TravelResponse;
import com.webservice.travel.service.TravelService;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * @RestController는 반환값을 JSON으로 응답 본문에 담아 보내는 컨트롤러라는 뜻이다.
 * @RequestMapping("/api/travels")는 이 클래스의 모든 경로 앞에 /api/travels를 붙인다.
 */
@RestController
@RequestMapping("/api/travels")
public class TravelController {
    private final TravelService service;

    public TravelController(TravelService service) {
        this.service = service;
    }

    // POST /api/travels : 등록. 201 Created와 생성된 객체를 돌려준다.
    @PostMapping
    public ResponseEntity<TravelResponse> create(@RequestBody TravelRequest request) {
        TravelResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/travels/" + response.id())).body(response);
    }

    // GET /api/travels : 전체 조회
    @GetMapping
    public List<TravelResponse> findAll() {
        return service.findAll();
    }

    /*
     * STEP 3. Query Method 검색 API.
     * 강의자료 14페이지처럼 GET /api/travels?keyword=... 형태로 쿼리스트링을 받는다.
     * 검색 API가 여러 개라 같은 경로를 쓰면 충돌하므로, @GetMapping(params = ...)로
     * "어떤 파라미터가 붙었을 때 이 메서드가 호출될지"를 구분한다.
     * 파라미터가 없으면 위의 findAll()이 호출된다.
     */

    // GET /api/travels?keyword=tour : 여행지 부분 일치 + 예산 내림차순
    @GetMapping(params = "keyword")
    public List<TravelResponse> searchByDestination(@RequestParam String keyword) {
        return service.findByDestinationKeyword(keyword);
    }

    // GET /api/travels?country=Japan : 국가 일치 (단일 조건)
    @GetMapping(params = "country")
    public List<TravelResponse> searchByCountry(@RequestParam String country) {
        return service.findByCountry(country);
    }

    /*
     * GET /api/travels?country=Japan&minDays=1&maxDays=5 : 국가 일치 And 여행일수 Between (복합 조건)
     * 파라미터 3개를 모두 요구하므로, country만 붙은 요청보다 이 메서드가 더 구체적으로 매칭된다.
     */
    @GetMapping(params = {"country", "minDays", "maxDays"})
    public List<TravelResponse> searchByCountryAndDuration(@RequestParam String country,
                                                           @RequestParam Integer minDays,
                                                           @RequestParam Integer maxDays) {
        return service.findByCountryAndDurationDaysBetween(country, minDays, maxDays);
    }

    // GET /api/travels?minBudget=500000 : 예산 초과 + 출발일 오름차순
    @GetMapping(params = "minBudget")
    public List<TravelResponse> searchByBudget(@RequestParam BigDecimal minBudget) {
        return service.findByMinBudget(minBudget);
    }

    // GET /api/travels/{id} : 단건 조회
    @GetMapping("/{id}")
    public TravelResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    // PUT /api/travels/{id} : 수정
    @PutMapping("/{id}")
    public TravelResponse update(@PathVariable Long id, @RequestBody TravelRequest request) {
        return service.update(id, request);
    }

    // DELETE /api/travels/{id} : 삭제. 204 No Content, 본문 없음
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
