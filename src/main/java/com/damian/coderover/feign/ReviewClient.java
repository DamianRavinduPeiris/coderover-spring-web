package com.damian.coderover.feign;

import com.damian.coderover.dto.ReviewResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "reviewClient", url = "${review.client.base-url}")
public interface ReviewClient {

    @PostMapping("/api/v1/chat/completions")
    ResponseEntity<ReviewResponseDTO> getCodeReview(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> requestBody
    );
}