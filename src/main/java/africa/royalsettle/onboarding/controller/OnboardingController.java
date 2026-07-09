package africa.royalsettle.onboarding.controller;

import africa.royalsettle.common.anotations.WrapResponse;
import africa.royalsettle.onboarding.dto.SetCustomerPinRequest;
import africa.royalsettle.onboarding.dto.SetCustomerPinResponse;
import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;
import africa.royalsettle.onboarding.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WrapResponse
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/customer")
@Tag(name = "Onboarding", description = "Customer registration operations")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/create-account")
    @Operation(
            summary = "Create a customer account",
            description = "Registers a customer and assigns the default Royal Settle user role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account created",
                    content = @Content(schema = @Schema(implementation = SignupResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate customer")
    })
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return onboardingService.signup(request);
    }

    @PostMapping("/setup-pin")
    @Operation(
            summary = "Set up customer transaction PIN",
            description = "Sets or updates the authenticated customer's transaction PIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "PIN set",
                    content = @Content(schema = @Schema(implementation = SetCustomerPinResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation error or PIN mismatch"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public SetCustomerPinResponse setupPin(@Valid @RequestBody SetCustomerPinRequest request) {
        return onboardingService.setupPin(request);
    }
}
