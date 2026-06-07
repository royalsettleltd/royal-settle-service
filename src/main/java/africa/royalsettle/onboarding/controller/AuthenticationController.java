package africa.royalsettle.onboarding.controller;

import africa.royalsettle.common.anotations.WrapResponse;
import africa.royalsettle.onboarding.dto.*;
import africa.royalsettle.onboarding.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@WrapResponse
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh-token")
    public LoginResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request);
    }

    @PostMapping("/logout")
    public LogoutResponse logout(
            HttpServletRequest request,
            @Valid @RequestBody LogoutRequest logoutRequest
    ) {
        return authenticationService.logout(request, logoutRequest);
    }
}
