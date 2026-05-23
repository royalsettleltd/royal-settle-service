package africa.royalsettle.onboarding.service;

import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;

public interface OnboardingService {

    SignupResponse signup(SignupRequest request);
}
