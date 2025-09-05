package com.damian.coderover.feign;

import com.damian.coderover.dto.CodeT5_V1_DTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "codeT5ReviewClient", url = "${codeT5.base-url}")
public interface CodeT5ReviewClient {

    @PostMapping("/predict/v1")
    ResponseEntity<CodeT5_V1_DTO> getV1Review(
            @RequestBody Map<String, String> requestBody
    );
}