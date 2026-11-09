package com.webservice.travel.global.exception;

import com.webservice.travel.global.exception.constant.ExceptionCode;

/*
 * 서비스 계층에서 던지는 예외. 어떤 상황인지 ExceptionCode로 함께 전달한다.
 * RuntimeException을 상속하므로 @Transactional이 걸린 메서드에서 던지면 롤백된다.
 */
public class CustomException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public CustomException(ExceptionCode exceptionCode) {
        super("");
        this.exceptionCode = exceptionCode;
    }

    public CustomException(ExceptionCode exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }
}
