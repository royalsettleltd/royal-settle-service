package africa.royalsettle.notification.service;

import africa.royalsettle.notification.dto.response.OtpResponse;
import africa.royalsettle.notification.dto.request.OtpSendRequest;
import africa.royalsettle.notification.dto.request.OtpVerifyRequest;

public interface OtpService {
    OtpResponse sendOtp(OtpSendRequest request);

    OtpResponse verifyOtp(OtpVerifyRequest request);
}
