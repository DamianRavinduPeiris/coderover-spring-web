package com.damian.coderover.service;

import com.damian.coderover.entity.User;
import com.damian.coderover.response.Response;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {

    ResponseEntity<Response> fetchUserInfo();

    User persistUser(User user);

    Optional<User> fetchUserByEmail(String email);
}
