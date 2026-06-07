package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.repository.OtpTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OtpTokenCleanupServiceTest {

    @Mock
    private OtpTokenRepository otpTokenRepository;

    @InjectMocks
    private OtpTokenCleanupService otpTokenCleanupService;

    @Test
    void deletesTokensExpiredBeforeCleanupRuns() {
        LocalDateTime beforeCleanup = LocalDateTime.now();

        otpTokenCleanupService.deleteExpiredTokens();

        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(otpTokenRepository).deleteByExpiresAtBefore(cutoffCaptor.capture());
        assertFalse(cutoffCaptor.getValue().isBefore(beforeCleanup));
    }
}
