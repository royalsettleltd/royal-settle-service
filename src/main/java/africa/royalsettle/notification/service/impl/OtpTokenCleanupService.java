package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.repository.OtpTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpTokenCleanupService {

    private final OtpTokenRepository otpTokenRepository;

    @Scheduled(fixedDelayString = "${notification.otp.cleanup-interval:PT15M}")
    @Transactional
    public void deleteExpiredTokens() {
        otpTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
