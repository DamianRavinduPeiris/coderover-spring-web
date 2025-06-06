package com.damian.coderover.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLES = "roles";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key signingKey;


    public String createToken(OAuth2User oAuth2User) {
        var claims = new HashMap<String,Object>();
        claims.put(CLAIM_EMAIL, oAuth2User.getAttribute(CLAIM_EMAIL));
        claims.put(CLAIM_NAME, oAuth2User.getAttribute(CLAIM_NAME));
        claims.put(CLAIM_ROLES, oAuth2User.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        return Jwts.builder()
                .subject(oAuth2User.getName())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public void afterPropertiesSet() {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
