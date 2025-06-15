package com.damian.coderover.service;

import com.damian.coderover.response.Response;
import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface UserService {
    ResponseEntity<Response> fetchUserInfo();
}
