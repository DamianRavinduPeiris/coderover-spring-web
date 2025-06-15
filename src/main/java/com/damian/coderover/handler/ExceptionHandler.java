package com.damian.coderover.handler;

import com.damian.coderover.exception.GithubException;
import com.damian.coderover.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(exception = GithubException.class)
    public ResponseEntity<Response> handleGithubException(GithubException ex) {
        var response = new Response("Inter Service error occurred : " + ex.getMessage(),
                null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.internalServerError().body(response);
    }
}
