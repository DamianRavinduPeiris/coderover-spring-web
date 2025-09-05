package com.damian.coderover.service.impl;

import com.damian.coderover.dto.CodeT5_V1_DTO;
import com.damian.coderover.dto.ReviewResponseDTO;
import com.damian.coderover.exception.ReviewException;
import com.damian.coderover.feign.CodeT5ReviewClient;
import com.damian.coderover.feign.ReviewClient;
import com.damian.coderover.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock ReviewClient reviewClient;
    @Mock CodeT5ReviewClient codeT5ReviewClient;

    @InjectMocks ReviewServiceImpl service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "reviewAuthToken", "secret");
        ReflectionTestUtils.setField(service, "reviewPrompt", "Please review:");
    }

    @Test
    void requestCodeReview_success() {
        var msg = new ReviewResponseDTO.Choice.Message();
        msg.setRole("assistant");
        msg.setContent("Looks good");
        var choice = new ReviewResponseDTO.Choice();
        choice.setMessage(msg);
        var dto = new ReviewResponseDTO();
        dto.setChoices(List.of(choice));
        when(reviewClient.getCodeReview(anyString(), any())).thenReturn(ResponseEntity.ok(dto));

        var resp = service.requestCodeReview("code");
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Response body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).contains("Code Review completed successfully");
        assertThat(body.data()).isInstanceOf(ReviewResponseDTO.class);
    }

    @Test
    void requestCodeReview_exceptionWraps() {
        when(reviewClient.getCodeReview(anyString(), any())).thenThrow(new RuntimeException("boom"));
        assertThrows(ReviewException.class, () -> service.requestCodeReview("code"));
    }

    @Test
    void requestCodeReviewFromCodeT5V1_success() {
        var dto = new CodeT5_V1_DTO();
        dto.setPrediction("ok");
        when(codeT5ReviewClient.getV1Review(any())).thenReturn(ResponseEntity.ok(dto));
        var resp = service.requestCodeReviewFromCodeT5V1("class A {}");
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().data()).isInstanceOf(CodeT5_V1_DTO.class);
    }

    @Test
    void requestCodeReviewFromCodeT5V1_exceptionWraps() {
        when(codeT5ReviewClient.getV1Review(any())).thenThrow(new RuntimeException("err"));
        assertThrows(ReviewException.class, () -> service.requestCodeReviewFromCodeT5V1("x"));
    }
}
