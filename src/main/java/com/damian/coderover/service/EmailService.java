package com.damian.coderover.service;

import com.damian.coderover.dto.ReportEmailDTO;
import com.damian.coderover.response.Response;
import org.springframework.http.ResponseEntity;

public interface EmailService {
    ResponseEntity<Response> sendReport(ReportEmailDTO dto);
}