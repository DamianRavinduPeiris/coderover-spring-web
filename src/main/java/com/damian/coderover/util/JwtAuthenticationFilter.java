package com.damian.coderover.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_PREFIX = "ROLE_";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var header = request.getHeader(AUTH_HEADER);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            log.debug("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        var token = header.substring(TOKEN_PREFIX.length());

        try {
            var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            var username = claims.getSubject();

            if (username == null || claims.getExpiration() == null) {
                log.warn("Invalid JWT token : missing subject or expiration");
                filterChain.doFilter(request, response);
                return;
            }

            // Safe role extraction
            var rawRoles = claims.get(ROLES_CLAIM);
            var authorities = new ArrayList<SimpleGrantedAuthority>();

            if (rawRoles instanceof List<?> roleList) {
                for (Object role : roleList) {
                    if (role instanceof String roleStr) {
                        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + roleStr));
                    } else {
                        log.warn("Skipping unexpected role type: {}", role.getClass());
                    }
                }
            } else if (rawRoles != null) {
                log.warn("Expected a list for 'roles' claim but got: {}", rawRoles.getClass());
            }
            log.info("Extracted roles from JWT: {}", authorities);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Authenticated user: {}", username);
            }

        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during JWT processing: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
