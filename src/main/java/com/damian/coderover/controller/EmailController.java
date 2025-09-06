package com.damian.coderover.controller;

import com.damian.coderover.dto.ReportEmailDTO;
import com.damian.coderover.response.Response;
import com.damian.coderover.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-report")
    public ResponseEntity<Response> sendReportEmail(@RequestBody ReportEmailDTO dto) {
        return emailService.sendReport(dto);
    }
}