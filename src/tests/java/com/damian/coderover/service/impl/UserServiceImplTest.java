package com.damian.coderover.service.impl;

import com.damian.coderover.dto.UserDTO;
import com.damian.coderover.entity.User;
import com.damian.coderover.repository.UserRepo;
import com.damian.coderover.response.Response;
import com.damian.coderover.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock JwtUtils jwtUtils;
    @Mock HttpServletRequest request;
    @Mock UserRepo userRepo;

    @InjectMocks UserServiceImpl service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "DEFAULT_PROFILE_PIC", "http://default/pic.png");
    }

    @Test
    void persistUser_returnsExisting_whenEmailExists() {
        var existing = User.builder().id("1").email("a@b.com").build();
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(existing));

        var result = service.persistUser(User.builder().email("a@b.com").build());
        assertThat(result).isSameAs(existing);
        verify(userRepo, never()).save(any());
    }

    @Test
    void persistUser_saves_whenEmailNotFound() {
        var toSave = User.builder().email("c@d.com").build();
        var saved = User.builder().id("2").email("c@d.com").build();
        when(userRepo.findByEmail("c@d.com")).thenReturn(Optional.empty());
        when(userRepo.save(toSave)).thenReturn(saved);

        var result = service.persistUser(toSave);
        assertThat(result).isSameAs(saved);
        verify(userRepo).save(toSave);
    }

    @Test
    void fetchUserByEmail_delegatesToRepo() {
        var user = User.builder().id("3").email("x@y.com").build();
        when(userRepo.findByEmail("x@y.com")).thenReturn(Optional.of(user));
        var opt = service.fetchUserByEmail("x@y.com");
        assertThat(opt).contains(user);
    }

    @Test
    void fetchUserInfo_noCookies_returnsUnauthorized() {
        when(request.getCookies()).thenReturn(null);
        var resp = service.fetchUserInfo();
        assertUnauthorized(resp);
    }

    @Test
    void fetchUserInfo_missingAccessToken_returnsUnauthorized() {
        when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("other", "val") });
        var resp = service.fetchUserInfo();
        assertUnauthorized(resp);
    }

    @Test
    void fetchUserInfo_tokenValid_userFound_returnsOkWithDtoAndDefaults() {
        // cookies
        when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("access_token", "jwt-token") });
        // claims
        Claims claims = mock(Claims.class);
        when(claims.get(eq("email"), eq(String.class))).thenReturn("found@user.com");
        when(jwtUtils.parseJwt("jwt-token")).thenReturn(claims);
        // user in repo with many nulls to test defaults
        var user = User.builder()
                .id("u1").name("John").email("found@user.com").login("john")
                .profilePicURL(null)
                .company("Acme").blog("blog").location("loc").bio("bio")
                .publicRepos(null).privateRepos(null).publicGists(null)
                .followers(null).following(null)
                .siteAdmin(null).twoFactorAuth(null)
                .accountType("user").planName("free").planSpace(null)
                .build();
        when(userRepo.findByEmail("found@user.com")).thenReturn(Optional.of(user));

        ResponseEntity<Response> resp = service.fetchUserInfo();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).contains("successfully fetched");
        assertThat(body.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.data()).isInstanceOf(UserDTO.class);
        var dto = (UserDTO) body.data();
        assertThat(dto.getId()).isEqualTo("u1");
        assertThat(dto.getProfilePicURL()).isEqualTo("http://default/pic.png");
        assertThat(dto.getPublicRepos()).isEqualTo(0);
        assertThat(dto.getPrivateRepos()).isEqualTo(0);
        assertThat(dto.getPublicGists()).isEqualTo(0);
        assertThat(dto.getFollowers()).isEqualTo(0);
        assertThat(dto.getFollowing()).isEqualTo(0);
        assertThat(dto.getSiteAdmin()).isFalse();
        assertThat(dto.getTwoFactorAuth()).isFalse();
        assertThat(dto.getPlanSpace()).isEqualTo(0L);
    }

    @Test
    void fetchUserInfo_tokenValid_userNotFound_returnsUnauthorized() {
        when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("access_token", "jwt-token") });
        Claims claims = mock(Claims.class);
        when(claims.get(eq("email"), eq(String.class))).thenReturn("none@user.com");
        when(jwtUtils.parseJwt("jwt-token")).thenReturn(claims);
        when(userRepo.findByEmail("none@user.com")).thenReturn(Optional.empty());

        var resp = service.fetchUserInfo();
        assertUnauthorized(resp);
    }

    private static void assertUnauthorized(ResponseEntity<Response> resp) {
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().message()).contains("Invalid token");
        assertThat(resp.getBody().statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
