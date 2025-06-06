package com.damian.coderover.config;

import com.damian.coderover.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_HEADER = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var oAuth2User = (OAuth2User) authentication.getPrincipal();
        var token = tokenProvider.createToken(oAuth2User);
        response.setHeader(AUTHORIZATION_HEADER, BEARER_HEADER + token);
        response.setHeader("Access-Control-Expose-Headers", AUTHORIZATION_HEADER);
        response.setStatus(HttpServletResponse.SC_OK);
        var requestCache = new org.springframework.security.web.savedrequest.HttpSessionRequestCache();
        var savedRequest = requestCache.getRequest(request, response);
        var redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : "/";
        log.info("Redirecting to : {}" , redirectUrl);
        response.sendRedirect(redirectUrl);

    }
}
