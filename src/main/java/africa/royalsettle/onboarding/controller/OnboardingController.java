package africa.royalsettle.onboarding.controller;

import africa.royalsettle.common.anotations.WrapResponse;
import africa.royalsettle.onboarding.dto.LoginRequest;
import africa.royalsettle.onboarding.dto.LoginResponse;
import africa.royalsettle.onboarding.dto.LogoutRequest;
import africa.royalsettle.onboarding.dto.LogoutResponse;
import africa.royalsettle.onboarding.dto.RefreshTokenRequest;
import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;
import africa.royalsettle.onboarding.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WrapResponse
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/customer")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/create-account")
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return onboardingService.signup(request);
    }
}
