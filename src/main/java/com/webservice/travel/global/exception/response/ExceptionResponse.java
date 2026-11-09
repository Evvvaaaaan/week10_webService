package com.webservice.travel.global.exception.response;

import org.springframework.http.HttpStatus;

import java.time.Instant;

/*
 * 예외가 났을 때 클라이언트에 내려 주는 응답 형식.
 * timestamp, status, code, message 네 값으로 항상 같은 모양을 유지한다.
 */
public record ExceptionResponse(
        Instant timestamp,
        HttpStatus status,
        String code,
        String message
) {
}
