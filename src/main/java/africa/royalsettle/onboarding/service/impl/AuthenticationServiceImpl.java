package africa.royalsettle.onboarding.service.impl;

import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.dto.*;
import africa.royalsettle.onboarding.service.AuthenticationService;
import africa.royalsettle.security.CustomUserDetailsService;
import africa.royalsettle.security.JwtAuthenticationFilter;
import africa.royalsettle.security.JwtTokenUtil;
import africa.royalsettle.security.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null
                || StringUtils.isBlank(request.getUsername())
                || StringUtils.isBlank(request.getPassword())) {
            throw new BadRequestException("Username and password are required");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername().trim(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return LoginResponse.builder()
                .username(userDetails.getUsername())
                .accessToken(jwtTokenUtil.generateToken(userDetails))
                .refreshToken(jwtTokenUtil.generateRefreshToken(userDetails))
                .tokenType("Bearer")
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken().trim();

        if (!jwtTokenUtil.validateToken(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            throw new BadRequestException("Invalid refresh token");
        }

        return LoginResponse.builder()
                .username(userDetails.getUsername())
                .accessToken(jwtTokenUtil.generateToken(userDetails))
                .refreshToken(jwtTokenUtil.generateRefreshToken(userDetails))
                .tokenType("Bearer")
                .build();
    }

    @Override
    public LogoutResponse logout(HttpServletRequest request) {
        String accessToken = extractAccessToken(request);
        blacklistToken(accessToken);

        return LogoutResponse.builder()
                .message("Logout successful")
                .build();
    }

    private String extractAccessToken(HttpServletRequest request) {
        Object accessToken = request.getAttribute(JwtAuthenticationFilter.JWT_REQUEST_ATTRIBUTE);
        if (!(accessToken instanceof String token) || StringUtils.isBlank(token)) {
            throw new BadRequestException("Authorization token is required");
        }
        return token;
    }

    private void blacklistToken(String token) {
        if (!jwtTokenUtil.validateToken(token)) {
            throw new BadRequestException("Invalid token");
        }

        Instant expiresAt = jwtTokenUtil.extractExpiration(token).toInstant();
        tokenBlacklistService.blacklist(token, expiresAt);
    }
}
