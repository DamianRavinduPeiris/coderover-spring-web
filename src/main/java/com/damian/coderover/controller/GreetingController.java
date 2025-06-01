package com.damian.coderover.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting() {
        return "hi junie";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}