package africa.royalsettle.security.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_session",
        indexes = @Index(name = "idx_refresh_session_expires_at", columnList = "expires_at")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshSession {

    @Id
    @Column(name = "session_id", nullable = false, updatable = false, length = 36)
    private String sessionId;

    @Column(nullable = false, updatable = false, length = 120)
    private String username;

    @Column(name = "refresh_token_id", nullable = false, unique = true, length = 36)
    private String refreshTokenId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
