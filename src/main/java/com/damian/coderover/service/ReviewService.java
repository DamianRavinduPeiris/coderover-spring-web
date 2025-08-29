package com.damian.coderover.service;

import com.damian.coderover.response.Response;

import org.springframework.http.ResponseEntity;


public interface ReviewService {
    ResponseEntity<Response> requestCodeReview(String code);
}