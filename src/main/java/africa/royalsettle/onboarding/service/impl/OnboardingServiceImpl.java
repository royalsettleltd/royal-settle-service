package africa.royalsettle.onboarding.service.impl;

import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.dto.LoginRequest;
import africa.royalsettle.onboarding.dto.LoginResponse;
import africa.royalsettle.onboarding.dto.LogoutRequest;
import africa.royalsettle.onboarding.dto.LogoutResponse;
import africa.royalsettle.onboarding.dto.RefreshTokenRequest;
import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;
import africa.royalsettle.onboarding.entity.UserRole;
import africa.royalsettle.onboarding.entity.Users;
import africa.royalsettle.onboarding.enums.RoleName;
import africa.royalsettle.onboarding.repository.UsersRepository;
import africa.royalsettle.onboarding.service.OnboardingService;
import africa.royalsettle.security.CustomUserDetailsService;
import africa.royalsettle.security.JwtTokenUtil;
import africa.royalsettle.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String BEARER = "Bearer ";

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
    public LogoutResponse logout(String authorizationHeader, LogoutRequest request) {
        String accessToken = extractBearerToken(authorizationHeader);
        blacklistToken(accessToken);

        if (request != null && StringUtils.isNotBlank(request.getRefreshToken())) {
            blacklistToken(request.getRefreshToken().trim());
        }

        return LogoutResponse.builder()
                .message("Logout successful")
                .build();
    }

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String emailAddress = request.getEmailAddress().trim().toLowerCase();
        String phoneNumber = request.getPhoneNumber().trim();

        if (usersRepository.existsByEmailAddress(emailAddress)) {
            throw new IllegalArgumentException("emailAddress already exists");
        }

        if (usersRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("phoneNumber already exists");
        }

        Users user = new Users();
        user.setUsername(emailAddress);
        user.setFullName(request.getFullName().trim());
        user.setEmailAddress(emailAddress);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setReferralCode(request.getReferralCode());

        UserRole role = UserRole.builder()
                .name(RoleName.ROYALSETTLE_USER)
                .user(user)
                .build();
        user.getRoles().add(role);

        Users savedUser = usersRepository.save(user);

        return SignupResponse.builder()
                .code(savedUser.getCode())
                .fullName(savedUser.getFullName())
                .emailAddress(savedUser.getEmailAddress())
                .phoneNumber(savedUser.getPhoneNumber())
                .referralCode(savedUser.getReferralCode())
                .build();
    }

    private String extractBearerToken(String authorizationHeader) {
        if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(BEARER)) {
            throw new BadRequestException("Authorization token is required");
        }
        return authorizationHeader.substring(BEARER.length());
    }

    private void blacklistToken(String token) {
        if (!jwtTokenUtil.validateToken(token)) {
            throw new BadRequestException("Invalid token");
        }

        Instant expiresAt = jwtTokenUtil.extractExpiration(token).toInstant();
        tokenBlacklistService.blacklist(token, expiresAt);
    }
}
