package com.damian.coderover.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportEmailDTO {
    private String[] defects;
    private String[] performance;
    private String[] vulnerabilities;
}
