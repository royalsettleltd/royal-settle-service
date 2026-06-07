package africa.royalsettle.notification.controller;

import africa.royalsettle.common.anotations.WrapResponse;
import africa.royalsettle.notification.dto.response.OtpResponse;
import africa.royalsettle.notification.dto.request.OtpSendRequest;
import africa.royalsettle.notification.dto.request.OtpVerifyRequest;
import africa.royalsettle.notification.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/otp")
@Tag(name = "OTP", description = "One-time password delivery and verification")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    @Operation(summary = "Send OTP", description = "Creates and sends a six-digit OTP by email or SMS.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP sent",
                    content = @Content(schema = @Schema(implementation = OtpResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid recipient or notification type")
    })
    public OtpResponse sendOtp(@Valid @RequestBody OtpSendRequest request) {
        return otpService.sendOtp(request);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Validates and consumes an unexpired OTP.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "OTP verified",
                    content = @Content(schema = @Schema(implementation = OtpResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "OTP is invalid or expired")
    })
    public OtpResponse verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return otpService.verifyOtp(request);
    }
}
