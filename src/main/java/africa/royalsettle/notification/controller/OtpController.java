package africa.royalsettle.notification.controller;

import africa.royalsettle.common.anotations.WrapResponse;
import africa.royalsettle.notification.dto.response.OtpResponse;
import africa.royalsettle.notification.dto.request.OtpSendRequest;
import africa.royalsettle.notification.dto.request.OtpVerifyRequest;
import africa.royalsettle.notification.service.OtpService;
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
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public OtpResponse sendOtp(@Valid @RequestBody OtpSendRequest request) {
        return otpService.sendOtp(request);
    }

    @PostMapping("/verify")
    public OtpResponse verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return otpService.verifyOtp(request);
    }
}
