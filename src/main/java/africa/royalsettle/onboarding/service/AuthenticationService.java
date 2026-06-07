package africa.royalsettle.onboarding.service;

import africa.royalsettle.onboarding.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    LogoutResponse logout(HttpServletRequest request, LogoutRequest logoutRequest);
}
