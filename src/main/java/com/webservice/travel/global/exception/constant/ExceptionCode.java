package com.webservice.travel.global.exception.constant;

import org.springframework.http.HttpStatus;

/*
 * 예외 상황별로 HTTP 상태 코드와 기본 메시지를 한 곳에 모아 둔 enum.
 * 예외 응답을 만들 때 이 값만 꺼내 쓰면 되므로, 상태 코드가 여기저기 흩어지지 않는다.
 */
public enum ExceptionCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "아이디를 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String message;

    ExceptionCode(
            final HttpStatus status,
            final String message
    ) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
