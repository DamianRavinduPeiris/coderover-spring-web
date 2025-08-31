package com.damian.coderover.util;

import com.damian.coderover.entity.User;
import com.damian.coderover.feign.GithubClient;
import com.damian.coderover.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class TokenProvider implements InitializingBean {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GithubClient githubClient;
    private final UserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key signingKey;

    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";
    private static final String PICTURE = "picture";
    private static final String AVATAR_URL = "avatar_url";

    @Override
    public void afterPropertiesSet() {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String issueTokenAndPersistUser(OAuth2User oAuth2User, Authentication authentication) {
        log.info("OAuth2 attributes: {}", oAuth2User.getAttributes());

        var claims = new HashMap<String, Object>();

        var email = oAuth2User.getAttribute(CLAIM_EMAIL);
        if (email == null && "github".equals(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId())) {
            email = fetchGithubEmail(authentication);
        }

        var profilePic = oAuth2User.getAttribute(PICTURE);
        if (profilePic == null) profilePic = oAuth2User.getAttribute(AVATAR_URL);
        String profilePicUrl = profilePic != null ? profilePic.toString() : null;

        claims.put(CLAIM_NAME, oAuth2User.getAttribute(CLAIM_NAME));
        claims.put(CLAIM_EMAIL, email);
        claims.put(CLAIM_ROLES, oAuth2User.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        claims.put(PICTURE, profilePicUrl);


        var user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(Optional.ofNullable(oAuth2User.getAttribute("name"))
                        .map(Object::toString)
                        .orElse(null))
                .email(email != null ? email.toString() : null)
                .login(Optional.ofNullable(oAuth2User.getAttribute("login"))
                        .map(Object::toString)
                        .orElse(null))
                .profilePicURL(profilePicUrl)
                .company(Optional.ofNullable(oAuth2User.getAttribute("company"))
                        .map(Object::toString)
                        .orElse(null))
                .blog(Optional.ofNullable(oAuth2User.getAttribute("blog"))
                        .map(Object::toString)
                        .orElse(null))
                .location(Optional.ofNullable(oAuth2User.getAttribute("location"))
                        .map(Object::toString)
                        .orElse(null))
                .bio(Optional.ofNullable(oAuth2User.getAttribute("bio"))
                        .map(Object::toString)
                        .orElse(null))
                .publicRepos(Optional.ofNullable(oAuth2User.getAttribute("public_repos"))
                        .map(v -> ((Number) v).intValue())
                        .orElse(0))
                .privateRepos(Optional.ofNullable(oAuth2User.getAttribute("total_private_repos"))
                        .map(v -> ((Number) v).intValue())
                        .orElse(0))
                .publicGists(Optional.ofNullable(oAuth2User.getAttribute("public_gists"))
                        .map(v -> ((Number) v).intValue())
                        .orElse(0))
                .followers(Optional.ofNullable(oAuth2User.getAttribute("followers"))
                        .map(v -> ((Number) v).intValue())
                        .orElse(0))
                .following(Optional.ofNullable(oAuth2User.getAttribute("following"))
                        .map(v -> ((Number) v).intValue())
                        .orElse(0))
                .siteAdmin(Optional.ofNullable(oAuth2User.getAttribute("site_admin"))
                        .map(v -> (Boolean) v)
                        .orElse(false))
                .twoFactorAuth(Optional.ofNullable(oAuth2User.getAttribute("two_factor_authentication"))
                        .map(v -> (Boolean) v)
                        .orElse(false))
                .accountType(Optional.ofNullable(oAuth2User.getAttribute("type"))
                        .map(Object::toString)
                        .orElse(null))
                .planName(Optional.ofNullable(oAuth2User.getAttribute("plan"))
                        .map(plan -> ((Map<?, ?>) plan).get("name"))
                        .map(Object::toString)
                        .orElse(null))
                .planSpace(Optional.ofNullable(oAuth2User.getAttribute("plan"))
                        .map(plan -> ((Map<?, ?>) plan).get("space"))
                        .map(v -> ((Number) v).longValue())
                        .orElse(0L))
                .build();


        var persistedUser = userService.persistUser(user);
        log.info("User logged in with email : {}", persistedUser.getEmail());

        return Jwts.builder()
                .subject(oAuth2User.getName())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey)
                .compact();
    }

    private String fetchGithubEmail(Authentication authentication) {
        List<Map<String, Object>> emails = githubClient.getUserEmails("Bearer " + getAccessToken((OAuth2AuthenticationToken) authentication));
        return emails.stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElse(null);
    }

    public String getAccessToken(OAuth2AuthenticationToken authentication) {
        var client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        return client.getAccessToken().getTokenValue();
    }
}
