package com.damian.coderover.filter;

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

    private static final String LOG_NO_TOKEN = "No JWT token found in request headers!";
    private static final String LOG_INVALID_TOKEN = "Invalid JWT token: missing subject or expiration";
    private static final String LOG_ROLE_TYPE_WARNING = "Skipping unexpected role type: {}";
    private static final String LOG_ROLE_FORMAT_WARNING = "Expected a list for 'roles' claim but got: {}";
    private static final String LOG_ROLES_EXTRACTED = "Extracted roles from JWT: {}";
    private static final String LOG_USER_AUTHENTICATED = "Authenticated user: {}";
    private static final String LOG_JWT_VALIDATION_FAILED = "JWT validation failed: {}";
    private static final String LOG_JWT_PROCESSING_ERROR = "Unexpected error during JWT processing: {}";

    private static final String EXC_JWT_INVALID = "Invalid JWT token: ";
    private static final String UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred while processing the JWT: ";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var header = request.getHeader(AUTH_HEADER);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            log.debug(LOG_NO_TOKEN);
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
                log.warn(LOG_INVALID_TOKEN);
                filterChain.doFilter(request, response);
                return;
            }

            var rawRoles = claims.get(ROLES_CLAIM);
            var authorities = new ArrayList<SimpleGrantedAuthority>();

            if (rawRoles instanceof List<?> roleList) {
                for (Object role : roleList) {
                    if (role instanceof String roleStr) {
                        authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + roleStr));
                    } else {
                        log.warn(LOG_ROLE_TYPE_WARNING, role.getClass());
                    }
                }
            } else if (rawRoles != null) {
                log.warn(LOG_ROLE_FORMAT_WARNING, rawRoles.getClass());
            }

            log.info(LOG_ROLES_EXTRACTED, authorities);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug(LOG_USER_AUTHENTICATED, username);
            }

        } catch (JwtException e) {
            log.error(LOG_JWT_VALIDATION_FAILED, e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, EXC_JWT_INVALID + e.getMessage());
            return;
        } catch (Exception e) {
            log.error(LOG_JWT_PROCESSING_ERROR, e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR_OCCURRED + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
