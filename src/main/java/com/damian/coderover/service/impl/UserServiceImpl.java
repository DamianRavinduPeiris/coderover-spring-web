package com.damian.coderover.service.impl;

import com.damian.coderover.response.Response;
import com.damian.coderover.service.UserService;
import com.damian.coderover.util.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;

    private static final String COOKIE_NAME = "access_token";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_PICTURE = "picture";
    private static final String DEFAULT_PROFILE_PIC =
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT1rTLeQraa9s-Rkj2_KMPOzh30CwK1G2D85A&s";
    private static final String SUCCESS_MSG = "User Info Successfully fetched!";
    private static final String ERROR_MSG = "Missing token";

    @Override
    public ResponseEntity<Response> fetchUserInfo() {
        var token = extractTokenFromCookies();

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Response(ERROR_MSG, null, HttpStatus.UNAUTHORIZED.value()));
        }

        var claims = jwtUtils.parseJwt(token);
        var name = claims.get(CLAIM_NAME, String.class);
        var profilePic = claims.get(CLAIM_PICTURE) != null
                ? claims.get(CLAIM_PICTURE).toString()
                : DEFAULT_PROFILE_PIC;

        var userData = Map.of(
                CLAIM_NAME, name,
                "profilePic", profilePic
        );

        return ResponseEntity.ok(new Response(SUCCESS_MSG, userData, HttpStatus.OK.value()));
    }

    private String extractTokenFromCookies() {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
