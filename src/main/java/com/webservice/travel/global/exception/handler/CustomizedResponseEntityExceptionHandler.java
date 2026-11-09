package com.webservice.travel.global.exception.handler;

import com.webservice.travel.global.exception.CustomException;
import com.webservice.travel.global.exception.response.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/*
 * @RestControllerAdvice는 모든 컨트롤러에서 발생한 예외를 한 곳에서 처리하게 해 준다.
 * 컨트롤러마다 try-catch를 반복하지 않아도 되고, 응답 형식도 통일된다.
 */
@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

    // 서비스에서 의도적으로 던진 CustomException 처리 (400, 404 등)
    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<ExceptionResponse> handleException(final CustomException ex) {

        final String message;
        if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
            message = ex.getExceptionCode().getMessage();
        } else {
            message = ex.getMessage();
        }

        final ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                ex.getExceptionCode().getStatus(),
                ex.getExceptionCode().name(),
                message
        );

        return new ResponseEntity<>(exceptionResponse, ex.getExceptionCode().getStatus());
    }

    // 위에서 잡지 못한 나머지 예외 처리 (500)
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllException(final Exception ex) {
        final ExceptionResponse exceptionResponse = new ExceptionResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Internal Server Error"
        );

        log.error("CustomizedResponseEntityExceptionHandler.handleAllException message:{}", ex.getMessage(), ex);

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
