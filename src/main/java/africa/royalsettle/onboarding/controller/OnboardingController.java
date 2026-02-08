package africa.royalsettle.onboarding.controller;

import africa.royalsettle.common.anotations.WrapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WrapResponse
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/onboarding")
public class OnboardingController {
}
