package com.damian.coderover.service.impl;

import com.damian.coderover.dto.UserDTO;
import com.damian.coderover.entity.User;
import com.damian.coderover.repository.UserRepo;
import com.damian.coderover.response.Response;
import com.damian.coderover.service.UserService;
import com.damian.coderover.util.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;
    private final UserRepo userRepo;
    @Value("${user.default-profile-picture-url}")
    private String DEFAULT_PROFILE_PIC;

    private static final String COOKIE_NAME = "access_token";
    private static final String CLAIM_EMAIL = "email";
    private static final String SUCCESS_MSG = "User info successfully fetched!";
    private static final String PLEASE_RE_AUTHENTICATE = "Invalid token,Please Re-Authenticate!";

    @Override
    public User persistUser(User user) {
        return userRepo.findByEmail(user.getEmail())
                .orElseGet(() -> userRepo.save(user));
    }

    @Override
    public Optional<User> fetchUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public ResponseEntity<Response> fetchUserInfo() {
        return Optional.ofNullable(extractTokenFromCookies())
                .map(token -> {
                    var claims = jwtUtils.parseJwt(token);
                    var email = claims.get(CLAIM_EMAIL, String.class);

                    return fetchUserByEmail(email)
                            .map(u -> ResponseEntity.ok(
                                    new Response(
                                            SUCCESS_MSG,
                                            toUserDTO(u),
                                            HttpStatus.OK.value()
                                    )
                            ))
                            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body(new Response(PLEASE_RE_AUTHENTICATE, null, HttpStatus.UNAUTHORIZED.value()))
                            );
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new Response(PLEASE_RE_AUTHENTICATE, null, HttpStatus.UNAUTHORIZED.value()))
                );
    }

    private String extractTokenFromCookies() {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private UserDTO toUserDTO(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .login(u.getLogin())
                .profilePicURL(u.getProfilePicURL() != null ? u.getProfilePicURL() : DEFAULT_PROFILE_PIC)
                .company(u.getCompany())
                .blog(u.getBlog())
                .location(u.getLocation())
                .bio(u.getBio())
                .publicRepos(u.getPublicRepos() != null ? u.getPublicRepos() : 0)
                .privateRepos(u.getPrivateRepos() != null ? u.getPrivateRepos() : 0)
                .publicGists(u.getPublicGists() != null ? u.getPublicGists() : 0)
                .followers(u.getFollowers() != null ? u.getFollowers() : 0)
                .following(u.getFollowing() != null ? u.getFollowing() : 0)
                .siteAdmin(u.getSiteAdmin() != null ? u.getSiteAdmin() : false)
                .twoFactorAuth(u.getTwoFactorAuth() != null ? u.getTwoFactorAuth() : false)
                .accountType(u.getAccountType())
                .planName(u.getPlanName())
                .planSpace(u.getPlanSpace() != null ? u.getPlanSpace() : 0L)
                .build();
    }
}
