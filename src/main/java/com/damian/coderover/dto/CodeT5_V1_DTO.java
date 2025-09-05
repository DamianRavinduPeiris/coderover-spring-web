package com.damian.coderover.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CodeT5_V1_DTO implements Serializable {
    private String prediction;
}