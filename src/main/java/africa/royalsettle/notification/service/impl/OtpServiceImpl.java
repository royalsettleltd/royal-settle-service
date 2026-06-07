package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.dto.request.NotificationRequest;
import africa.royalsettle.notification.dto.enums.NotificationType;
import africa.royalsettle.notification.service.EmailService;
import africa.royalsettle.notification.service.SmsService;
import africa.royalsettle.notification.dto.response.OtpResponse;
import africa.royalsettle.notification.dto.request.OtpSendRequest;
import africa.royalsettle.notification.dto.request.OtpVerifyRequest;
import africa.royalsettle.notification.models.OtpToken;
import africa.royalsettle.notification.repository.OtpTokenRepository;
import africa.royalsettle.notification.service.EmailTemplateService;
import africa.royalsettle.notification.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpServiceImpl implements OtpService {

    private static final Duration OTP_TTL = Duration.ofMinutes(10);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final SmsService smsService;

    @Value("${spring.mail.username:RoyalSettle}")
    private String emailSender;

    @Override
    public OtpResponse sendOtp(OtpSendRequest request) {
        String recipient = validateRecipient(request.getNotificationType(), request.recipient());
        String otp = generateOtp();

        otpTokenRepository.deleteByNotificationTypeAndRecipient(request.getNotificationType(), recipient);
        otpTokenRepository.save(OtpToken.builder()
                .notificationType(request.getNotificationType())
                .recipient(recipient)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plus(OTP_TTL))
                .build());

        sendNotification(request.getNotificationType(), recipient, otp);

        return OtpResponse.builder()
                .message("OTP sent successfully")
                .verified(false)
                .build();
    }

    @Override
    public OtpResponse verifyOtp(OtpVerifyRequest request) {
        String recipient = validateRecipient(request.getNotificationType(), request.recipient());
        OtpToken otpToken = otpTokenRepository
                .findFirstByNotificationTypeAndRecipientAndOtpAndExpiresAtAfterOrderByIdDesc(
                        request.getNotificationType(),
                        recipient,
                        request.getOtp(),
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));

        otpTokenRepository.delete(otpToken);

        return OtpResponse.builder()
                .message("OTP verified successfully")
                .verified(true)
                .build();
    }

    private String validateRecipient(NotificationType notificationType, String recipient) {
        if (StringUtils.isBlank(recipient)) {
            String field = notificationType == NotificationType.EMAIL ? "emailAddress" : "phoneNumber";
            throw new IllegalArgumentException(field + " is required");
        }
        return notificationType == NotificationType.EMAIL
                ? recipient.trim().toLowerCase()
                : recipient.trim();
    }

    @Async
    public void sendNotification(NotificationType notificationType, String recipient, String otp) {
        NotificationRequest request = new NotificationRequest();
        request.setNotificationType(notificationType);
        request.setRecipient(recipient);

        if (notificationType == NotificationType.EMAIL) {
            request.setSender(emailSender);
            request.setSubject("Token Verification");
            request.setMessage(emailTemplateService.buildOtpEmail(otp));
            emailService.send(request);
            return;
        }

        request.setMessage("Your token is " + otp + ". It expires in 10 minutes.");
        smsService.sendSms(request);
    }

    private String generateOtp() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
