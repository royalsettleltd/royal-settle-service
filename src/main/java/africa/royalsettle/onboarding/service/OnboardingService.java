package africa.royalsettle.onboarding.service;

import africa.royalsettle.onboarding.dto.LoginRequest;
import africa.royalsettle.onboarding.dto.LoginResponse;
import africa.royalsettle.onboarding.dto.LogoutRequest;
import africa.royalsettle.onboarding.dto.LogoutResponse;
import africa.royalsettle.onboarding.dto.RefreshTokenRequest;
import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;

public interface OnboardingService {
    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    LogoutResponse logout(String authorizationHeader, LogoutRequest request);

    SignupResponse signup(SignupRequest request);
}
