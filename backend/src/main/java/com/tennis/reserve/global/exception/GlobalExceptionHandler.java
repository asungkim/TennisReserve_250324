package com.tennis.reserve.global.exception;

import com.tennis.reserve.global.dto.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서비스 예외 처리
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus // Spring Docs 핸들러 인식
    public ResponseEntity<RsData<Void>> ServiceExceptionHandle(ServiceException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(
                        new RsData<>(
                                e.getCode(),
                                e.getMsg()
                        )
                );
    }

    // ReqForm 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("\n"));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new RsData<>(
                                "400-1",
                                message
                        )
                );
    }
}
