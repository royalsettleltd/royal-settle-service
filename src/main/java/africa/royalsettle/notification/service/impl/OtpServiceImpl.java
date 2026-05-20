package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.dto.request.NotificationRequest;
import africa.royalsettle.notification.dto.enums.NotificationType;
import africa.royalsettle.notification.service.EmailService;
import africa.royalsettle.notification.service.SmsService;
import africa.royalsettle.notification.dto.response.OtpResponse;
import africa.royalsettle.notification.dto.request.OtpSendRequest;
import africa.royalsettle.notification.dto.request.OtpVerifyRequest;
import africa.royalsettle.notification.service.EmailTemplateService;
import africa.royalsettle.notification.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final Duration OTP_TTL = Duration.ofMinutes(10);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String OTP_KEY_PREFIX = "otp:";

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final SmsService smsService;

    @Value("${spring.mail.username:RoyalSettle}")
    private String emailSender;

    @Override
    public OtpResponse sendOtp(OtpSendRequest request) {
        String recipient = validateRecipient(request.getNotificationType(), request.recipient());
        String otp = generateOtp();

        redisTemplate.opsForValue().set(buildKey(request.getNotificationType(), recipient), otp, OTP_TTL);
        sendNotification(request.getNotificationType(), recipient, otp);

        return OtpResponse.builder()
                .message("OTP sent successfully")
                .verified(false)
                .build();
    }

    @Override
    public OtpResponse verifyOtp(OtpVerifyRequest request) {
        String recipient = validateRecipient(request.getNotificationType(), request.recipient());
        String key = buildKey(request.getNotificationType(), recipient);
        String savedOtp = redisTemplate.opsForValue().get(key);

        if (!Objects.equals(savedOtp, request.getOtp())) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        redisTemplate.delete(key);

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

    private void sendNotification(NotificationType notificationType, String recipient, String otp) {
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

    private String buildKey(NotificationType notificationType, String recipient) {
        return OTP_KEY_PREFIX + notificationType.name() + ":" + recipient;
    }

    private String generateOtp() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
