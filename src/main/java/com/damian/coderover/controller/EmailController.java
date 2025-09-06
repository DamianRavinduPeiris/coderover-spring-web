package com.damian.coderover.controller;

import com.damian.coderover.dto.ReportEmailDTO;
import com.damian.coderover.response.Response;
import com.damian.coderover.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-report")
    public ResponseEntity<Response> sendReportEmail(@RequestBody ReportEmailDTO dto) {
        return emailService.sendReport(dto);
    }
}