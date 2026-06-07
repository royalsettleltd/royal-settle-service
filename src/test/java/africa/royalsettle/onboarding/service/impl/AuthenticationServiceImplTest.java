package africa.royalsettle.onboarding.service.impl;

import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.dto.LoginRequest;
import africa.royalsettle.onboarding.dto.LoginResponse;
import africa.royalsettle.onboarding.dto.LogoutRequest;
import africa.royalsettle.onboarding.dto.LogoutResponse;
import africa.royalsettle.onboarding.dto.RefreshTokenRequest;
import africa.royalsettle.security.service.CustomUserDetailsService;
import africa.royalsettle.security.service.RefreshSessionService;
import africa.royalsettle.security.util.JwtAuthenticationFilter;
import africa.royalsettle.security.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private RefreshSessionService refreshSessionService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void loginCreatesSessionAndReturnsTokenPair() {
        LoginRequest request = new LoginRequest();
        request.setUsername(" user@example.com ");
        request.setPassword("password123");
        UserDetails userDetails = userDetails();
        Date expirationDate = Date.from(Instant.now().plusSeconds(3600));
        Instant expiresAt = expirationDate.toInstant();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(eq(userDetails), anyString())).thenReturn("access-token");
        when(jwtTokenUtil.generateRefreshToken(eq(userDetails), anyString())).thenReturn("refresh-token");
        when(jwtTokenUtil.extractTokenId("refresh-token")).thenReturn("refresh-token-id");
        when(jwtTokenUtil.extractExpiration("refresh-token")).thenReturn(expirationDate);

        LoginResponse response = authenticationService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertEquals("user@example.com", authenticationCaptor.getValue().getPrincipal());
        assertEquals("password123", authenticationCaptor.getValue().getCredentials());

        ArgumentCaptor<String> accessSessionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> refreshSessionCaptor = ArgumentCaptor.forClass(String.class);
        verify(jwtTokenUtil).generateToken(eq(userDetails), accessSessionCaptor.capture());
        verify(jwtTokenUtil).generateRefreshToken(eq(userDetails), refreshSessionCaptor.capture());
        assertEquals(accessSessionCaptor.getValue(), refreshSessionCaptor.getValue());
        verify(refreshSessionService).create(
                accessSessionCaptor.getValue(),
                "user@example.com",
                "refresh-token-id",
                expiresAt
        );
        assertEquals("user@example.com", response.getUsername());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void loginRejectsMissingCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@example.com");
        request.setPassword(" ");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authenticationService.login(request)
        );

        assertEquals("Username and password are required", exception.getMessage());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void refreshRotatesSessionAndReturnsNewTokenPair() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(" old-refresh-token ");
        UserDetails userDetails = userDetails();
        Date expirationDate = Date.from(Instant.now().plusSeconds(7200));
        Instant expiresAt = expirationDate.toInstant();

        when(jwtTokenUtil.validateToken("old-refresh-token")).thenReturn(true);
        when(jwtTokenUtil.isRefreshToken("old-refresh-token")).thenReturn(true);
        when(jwtTokenUtil.extractUsername("old-refresh-token")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("old-refresh-token", userDetails)).thenReturn(true);
        when(jwtTokenUtil.extractSessionId("old-refresh-token")).thenReturn("session-id");
        when(jwtTokenUtil.generateToken(userDetails, "session-id")).thenReturn("new-access-token");
        when(jwtTokenUtil.generateRefreshToken(userDetails, "session-id")).thenReturn("new-refresh-token");
        when(jwtTokenUtil.extractTokenId("old-refresh-token")).thenReturn("old-token-id");
        when(jwtTokenUtil.extractTokenId("new-refresh-token")).thenReturn("new-token-id");
        when(jwtTokenUtil.extractExpiration("new-refresh-token")).thenReturn(expirationDate);
        when(refreshSessionService.rotate(
                "session-id",
                "user@example.com",
                "old-token-id",
                "new-token-id",
                expiresAt
        )).thenReturn(true);

        LoginResponse response = authenticationService.refreshToken(request);

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void refreshRejectsInvalidTokenBeforeLoadingUser() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");
        when(jwtTokenUtil.validateToken("invalid-token")).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authenticationService.refreshToken(request)
        );

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(refreshSessionService, never()).rotate(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any(Instant.class)
        );
    }

    @Test
    void refreshRejectsFailedSessionRotation() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");
        UserDetails userDetails = userDetails();
        Date expirationDate = Date.from(Instant.now().plusSeconds(7200));
        Instant expiresAt = expirationDate.toInstant();

        when(jwtTokenUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtTokenUtil.isRefreshToken("refresh-token")).thenReturn(true);
        when(jwtTokenUtil.extractUsername("refresh-token")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtTokenUtil.validateToken("refresh-token", userDetails)).thenReturn(true);
        when(jwtTokenUtil.extractSessionId("refresh-token")).thenReturn("session-id");
        when(jwtTokenUtil.generateToken(userDetails, "session-id")).thenReturn("new-access-token");
        when(jwtTokenUtil.generateRefreshToken(userDetails, "session-id")).thenReturn("new-refresh-token");
        when(jwtTokenUtil.extractTokenId("refresh-token")).thenReturn("old-token-id");
        when(jwtTokenUtil.extractTokenId("new-refresh-token")).thenReturn("new-token-id");
        when(jwtTokenUtil.extractExpiration("new-refresh-token")).thenReturn(expirationDate);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authenticationService.refreshToken(request)
        );

        assertEquals("Invalid refresh token", exception.getMessage());
    }

    @Test
    void logoutRevokesMatchingSession() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setRefreshToken(" refresh-token ");
        when(httpServletRequest.getAttribute(JwtAuthenticationFilter.JWT_REQUEST_ATTRIBUTE))
                .thenReturn("access-token");
        when(jwtTokenUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtTokenUtil.isRefreshToken("refresh-token")).thenReturn(true);
        when(jwtTokenUtil.extractSessionId("access-token")).thenReturn("session-id");
        when(jwtTokenUtil.extractSessionId("refresh-token")).thenReturn("session-id");
        when(jwtTokenUtil.extractUsername("access-token")).thenReturn("user@example.com");
        when(jwtTokenUtil.extractUsername("refresh-token")).thenReturn("user@example.com");
        when(jwtTokenUtil.extractTokenId("refresh-token")).thenReturn("refresh-token-id");
        when(refreshSessionService.revoke(
                "session-id",
                "user@example.com",
                "refresh-token-id"
        )).thenReturn(true);

        LogoutResponse response = authenticationService.logout(httpServletRequest, logoutRequest);

        assertEquals("Logout successful", response.getMessage());
    }

    @Test
    void logoutRejectsRefreshTokenFromDifferentSession() {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setRefreshToken("refresh-token");
        when(httpServletRequest.getAttribute(JwtAuthenticationFilter.JWT_REQUEST_ATTRIBUTE))
                .thenReturn("access-token");
        when(jwtTokenUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtTokenUtil.isRefreshToken("refresh-token")).thenReturn(true);
        when(jwtTokenUtil.extractSessionId("access-token")).thenReturn("access-session");
        when(jwtTokenUtil.extractSessionId("refresh-token")).thenReturn("refresh-session");
        when(jwtTokenUtil.extractUsername("access-token")).thenReturn("user@example.com");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authenticationService.logout(httpServletRequest, logoutRequest)
        );

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(refreshSessionService, never()).revoke(anyString(), anyString(), anyString());
    }

    private UserDetails userDetails() {
        return new User(
                "user@example.com",
                "encoded-password",
                AuthorityUtils.createAuthorityList("ROYALSETTLE_USER")
        );
    }
}
