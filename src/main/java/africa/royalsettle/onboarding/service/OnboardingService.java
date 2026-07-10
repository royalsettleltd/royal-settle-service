package africa.royalsettle.onboarding.service;

import africa.royalsettle.onboarding.dto.*;

public interface OnboardingService {

    LoginResponse signup(SignupRequest request);

    SetCustomerPinResponse setupPin(SetCustomerPinRequest request);
}
