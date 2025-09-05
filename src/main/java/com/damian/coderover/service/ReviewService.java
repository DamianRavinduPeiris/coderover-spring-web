package com.damian.coderover.service;

import com.damian.coderover.response.Response;

import org.springframework.http.ResponseEntity;


public interface ReviewService {
    ResponseEntity<Response> requestCodeReview(String code);
    ResponseEntity<Response> requestCodeReviewFromCodeT5V1(String code);
}