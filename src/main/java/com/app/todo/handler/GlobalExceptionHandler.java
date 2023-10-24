package com.app.todo.handler;

import com.app.todo.dto.FailureResponseHandler;
import io.jsonwebtoken.JwtException;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public FailureResponseHandler handleInvalidArgument(MethodArgumentNotValidException ex) {
        return FailureResponseHandler.builder()
                .error("Invalid Data, Please try again")
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException.class)
    public ResponseEntity jwtException(JwtException ex) {
        return ResponseEntity
                .badRequest()
                .body(FailureResponseHandler.builder()
                        .ok(false)
                        .error(ex.getMessage())
                        .status(401)
                        .build());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AuthenticationException.class)
    public FailureResponseHandler handleInvalidArgument(AuthenticationException ex) {
        return FailureResponseHandler.builder()
                .error("Authentication Filed")
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public FailureResponseHandler handleInvalidArgument(Exception ex) {
        return FailureResponseHandler.builder()
                .error(ex.getMessage())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

}