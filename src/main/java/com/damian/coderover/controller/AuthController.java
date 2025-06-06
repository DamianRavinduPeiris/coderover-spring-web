package com.damian.coderover.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    @GetMapping("/token")
    public String getToken(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                           @AuthenticationPrincipal OAuth2User principal) {
        /*log all the  info from principal and client*/
        log.info("Principal: {}", principal.getAttributes());
        log.info("Authorized Client: {}", authorizedClient.getClientRegistration().getClientId());
        log.info("Authorized Client Scopes: {}", authorizedClient.getClientRegistration().getScopes());
        log.info("Authorized Client Access Token: " + authorizedClient.getAccessToken().getTokenValue());
        log.info("Authorized Client Refresh Token: " + authorizedClient.getRefreshToken());
        log.info("Authorized Client Token Expiration: " + authorizedClient.getAccessToken().getExpiresAt());
        log.info("Authorized Client Token Type: " + authorizedClient.getAccessToken().getTokenType().getValue());
        log.info("Authorized Client Principal Name: " + principal.getName());
        log.info("Authorized Client Principal Attributes: " + principal.getAttributes());
        log.info("Authorized Client Principal Authorities: " + principal.getAuthorities());
        log.info("Authorized Client Principal Email: " + principal.getAttribute("email"));
        log.info("Authorized Client Principal Name: " + principal.getAttribute("name"));
        log.info("Authorized Client Principal Login: " + principal.getAttribute("login"));
        log.info("Authorized Client Principal Avatar URL: " + principal.getAttribute("avatar_url"));
        log.info("Authorized Client Principal ID: " + principal.getAttribute("id"));
        log.info("Authorized Client Principal Type: " + principal.getClass().getName());
        log.info("Authorized Client Principal Class: " + principal.getClass());
        log.info("Authorized Client Principal Class Simple Name: " + principal.getClass().getSimpleName());
        log.info("Authorized Client Principal Class Canonical Name: " + principal.getClass().getCanonicalName());
        log.info("Authorized Client Principal Class Name: " + principal.getClass().getName());
        log.info("Authorized Client Principal Class Package: " + principal.getClass().getPackage());
        log.info("Authorized Client Principal Class Package Name: " + principal.getClass().getPackageName());
        log.info("Authorized Client Principal Class Type Name: " + principal.getClass().getTypeName());
        log.info("Authorized Client Principal Class Enclosing Class: " + principal.getClass().getEnclosingClass());
        log.info("Authorized Client Principal Class Enclosing Method: " + principal.getClass().getEnclosingMethod());
        return "Access Token: " + authorizedClient.getAccessToken().getTokenValue();
    }

    @GetMapping(path = "/check")
    public String check(){
        return "ok!!!!!!!!!!!!!!";
    }
}
