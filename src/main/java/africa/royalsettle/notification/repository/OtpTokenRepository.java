package africa.royalsettle.notification.repository;

import africa.royalsettle.notification.dto.enums.NotificationType;
import africa.royalsettle.notification.models.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findFirstByNotificationTypeAndRecipientAndOtpAndExpiresAtAfterOrderByIdDesc(
            NotificationType notificationType,
            String recipient,
            String otp,
            LocalDateTime now
    );

    void deleteByNotificationTypeAndRecipient(NotificationType notificationType, String recipient);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
