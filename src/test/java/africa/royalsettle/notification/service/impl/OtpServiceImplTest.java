package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.dto.enums.NotificationType;
import africa.royalsettle.notification.dto.request.NotificationRequest;
import africa.royalsettle.notification.dto.request.OtpSendRequest;
import africa.royalsettle.notification.dto.request.OtpVerifyRequest;
import africa.royalsettle.notification.dto.response.OtpResponse;
import africa.royalsettle.notification.models.OtpToken;
import africa.royalsettle.notification.repository.OtpTokenRepository;
import africa.royalsettle.notification.service.EmailService;
import africa.royalsettle.notification.service.EmailTemplateService;
import africa.royalsettle.notification.service.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private OtpTokenRepository otpTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private OtpServiceImpl otpService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(otpService, "emailSender", "noreply@royalsettle.africa");
    }

    @Test
    void sendsEmailOtpAndReplacesExistingToken() {
        OtpSendRequest request = new OtpSendRequest();
        request.setNotificationType(NotificationType.EMAIL);
        request.setEmailAddress(" User@Example.com ");
        when(emailTemplateService.buildOtpEmail(any())).thenReturn("<p>OTP</p>");

        LocalDateTime beforeSend = LocalDateTime.now();
        OtpResponse response = otpService.sendOtp(request);

        verify(otpTokenRepository).deleteByNotificationTypeAndRecipient(
                NotificationType.EMAIL,
                "user@example.com"
        );

        ArgumentCaptor<OtpToken> tokenCaptor = ArgumentCaptor.forClass(OtpToken.class);
        verify(otpTokenRepository).save(tokenCaptor.capture());
        OtpToken token = tokenCaptor.getValue();
        assertEquals(NotificationType.EMAIL, token.getNotificationType());
        assertEquals("user@example.com", token.getRecipient());
        assertTrue(token.getOtp().matches("\\d{6}"));
        assertFalse(token.getExpiresAt().isBefore(beforeSend.plusMinutes(10)));

        ArgumentCaptor<NotificationRequest> notificationCaptor =
                ArgumentCaptor.forClass(NotificationRequest.class);
        verify(emailService).send(notificationCaptor.capture());
        NotificationRequest notification = notificationCaptor.getValue();
        assertEquals("noreply@royalsettle.africa", notification.getSender());
        assertEquals("user@example.com", notification.getRecipient());
        assertEquals("Token Verification", notification.getSubject());
        assertEquals("<p>OTP</p>", notification.getMessage());
        verify(emailTemplateService).buildOtpEmail(token.getOtp());
        verify(smsService, never()).sendSms(any());
        assertEquals("OTP sent successfully", response.getMessage());
        assertFalse(response.isVerified());
    }

    @Test
    void sendsSmsOtp() {
        OtpSendRequest request = new OtpSendRequest();
        request.setNotificationType(NotificationType.SMS);
        request.setPhoneNumber(" +2348012345678 ");

        OtpResponse response = otpService.sendOtp(request);

        ArgumentCaptor<OtpToken> tokenCaptor = ArgumentCaptor.forClass(OtpToken.class);
        verify(otpTokenRepository).save(tokenCaptor.capture());

        ArgumentCaptor<NotificationRequest> notificationCaptor =
                ArgumentCaptor.forClass(NotificationRequest.class);
        verify(smsService).sendSms(notificationCaptor.capture());
        NotificationRequest notification = notificationCaptor.getValue();
        assertEquals("+2348012345678", notification.getRecipient());
        assertTrue(notification.getMessage().contains(tokenCaptor.getValue().getOtp()));
        verify(emailService, never()).send(any());
        assertFalse(response.isVerified());
    }

    @Test
    void verifiesAndConsumesValidOtp() {
        OtpVerifyRequest request = new OtpVerifyRequest();
        request.setNotificationType(NotificationType.EMAIL);
        request.setEmailAddress(" User@Example.com ");
        request.setOtp("123456");
        OtpToken token = OtpToken.builder()
                .notificationType(NotificationType.EMAIL)
                .recipient("user@example.com")
                .otp("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        when(otpTokenRepository
                .findFirstByNotificationTypeAndRecipientAndOtpAndExpiresAtAfterOrderByIdDesc(
                        eq(NotificationType.EMAIL),
                        eq("user@example.com"),
                        eq("123456"),
                        any(LocalDateTime.class)
                ))
                .thenReturn(Optional.of(token));

        OtpResponse response = otpService.verifyOtp(request);

        verify(otpTokenRepository).delete(token);
        assertTrue(response.isVerified());
        assertEquals("OTP verified successfully", response.getMessage());
    }

    @Test
    void rejectsInvalidOrExpiredOtp() {
        OtpVerifyRequest request = new OtpVerifyRequest();
        request.setNotificationType(NotificationType.SMS);
        request.setPhoneNumber("+2348012345678");
        request.setOtp("123456");
        when(otpTokenRepository
                .findFirstByNotificationTypeAndRecipientAndOtpAndExpiresAtAfterOrderByIdDesc(
                        eq(NotificationType.SMS),
                        eq("+2348012345678"),
                        eq("123456"),
                        any(LocalDateTime.class)
                ))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> otpService.verifyOtp(request)
        );

        assertEquals("Invalid or expired OTP", exception.getMessage());
        verify(otpTokenRepository, never()).delete(any(OtpToken.class));
    }

    @Test
    void rejectsMissingRecipient() {
        OtpSendRequest request = new OtpSendRequest();
        request.setNotificationType(NotificationType.EMAIL);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> otpService.sendOtp(request)
        );

        assertEquals("emailAddress is required", exception.getMessage());
        verify(otpTokenRepository, never()).save(any());
    }
}
