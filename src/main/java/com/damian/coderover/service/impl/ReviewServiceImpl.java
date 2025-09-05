package com.damian.coderover.service.impl;

import com.damian.coderover.exception.ReviewException;
import com.damian.coderover.feign.CodeT5ReviewClient;
import com.damian.coderover.feign.ReviewClient;
import com.damian.coderover.response.Response;
import com.damian.coderover.service.ReviewService;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final CodeT5ReviewClient codeT5ReviewClient;
    private final ReviewClient reviewClient;
    @Value("${review.client.token}")
    private String reviewAuthToken;

    @Value("${review.client.prompt}")
    private String reviewPrompt;

    @Override
    public ResponseEntity<Response> requestCodeReview(String code) {
        try {
            var requestBody = Map.of(
                    "model", "openai/gpt-oss-120b:free",
                    "messages", List.of(
                            Map.of("role", "user", "content", reviewPrompt + "\n" + code)
                    )
            );
            var authHeader = "Bearer " + reviewAuthToken;
            var response = reviewClient.getCodeReview(authHeader, requestBody);
            var extractedResponse = response.getBody();
            log.info("Review response : {}", extractedResponse);
            return ResponseEntity.ok(new Response("Code Review completed successfully",
                    extractedResponse, HttpStatus.OK.value()));
        } catch (Exception e) {
            throw new ReviewException(("An error occurred while reviewing code : " + e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Response> requestCodeReviewFromCodeT5V1(String code) {
        var requestBody = Map.of("code", code);
        try {
            var response = codeT5ReviewClient.getV1Review(requestBody);
            var extractedResponse = response.getBody();
            log.info("CodeT5 v1 Review response : {}", extractedResponse);

            return ResponseEntity.ok(new Response("Code Review completed successfully!",
                    extractedResponse, HttpStatus.OK.value()));
        } catch (Exception e) {
            throw new ReviewException(("An error occurred while reviewing code from CodeT5 : " + e.getMessage()));
        }
    }
}