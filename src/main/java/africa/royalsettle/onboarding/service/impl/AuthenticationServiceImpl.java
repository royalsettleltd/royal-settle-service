package africa.royalsettle.onboarding.service.impl;

import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.dto.*;
import africa.royalsettle.onboarding.service.AuthenticationService;
import africa.royalsettle.security.service.CustomUserDetailsService;
import africa.royalsettle.security.util.JwtAuthenticationFilter;
import africa.royalsettle.security.util.JwtTokenUtil;
import africa.royalsettle.security.service.RefreshSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshSessionService refreshSessionService;

    @Override
    @Transactional
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
        String sessionId = UUID.randomUUID().toString();
        String accessToken = jwtTokenUtil.generateToken(userDetails, sessionId);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails, sessionId);
        refreshSessionService.create(
                sessionId,
                userDetails.getUsername(),
                jwtTokenUtil.extractTokenId(refreshToken),
                jwtTokenUtil.extractExpiration(refreshToken).toInstant()
        );

        return LoginResponse.builder()
                .username(userDetails.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken().trim();

        if (!jwtTokenUtil.validateToken(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtTokenUtil.validateToken(refreshToken, userDetails)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String sessionId = jwtTokenUtil.extractSessionId(refreshToken);
        String newAccessToken = jwtTokenUtil.generateToken(userDetails, sessionId);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails, sessionId);

        boolean rotated = refreshSessionService.rotate(
                sessionId,
                username,
                jwtTokenUtil.extractTokenId(refreshToken),
                jwtTokenUtil.extractTokenId(newRefreshToken),
                jwtTokenUtil.extractExpiration(newRefreshToken).toInstant()
        );

        if (!rotated) {
            throw new BadRequestException("Invalid refresh token");
        }

        return LoginResponse.builder()
                .username(userDetails.getUsername())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public LogoutResponse logout(HttpServletRequest request, LogoutRequest logoutRequest) {
        String accessToken = extractAccessToken(request);
        String refreshToken = validateRefreshToken(logoutRequest.getRefreshToken());

        String accessSessionId = jwtTokenUtil.extractSessionId(accessToken);
        String refreshSessionId = jwtTokenUtil.extractSessionId(refreshToken);
        String username = jwtTokenUtil.extractUsername(accessToken);

        if (!accessSessionId.equals(refreshSessionId)
                || !username.equals(jwtTokenUtil.extractUsername(refreshToken))
                || !refreshSessionService.revoke(
                        refreshSessionId,
                        username,
                        jwtTokenUtil.extractTokenId(refreshToken)
                )) {
            throw new BadRequestException("Invalid refresh token");
        }

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

    private String validateRefreshToken(String token) {
        String refreshToken = token.trim();
        if (!jwtTokenUtil.validateToken(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        return refreshToken;
    }
}
