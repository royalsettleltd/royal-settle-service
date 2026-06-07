package africa.royalsettle.security.repository;

import africa.royalsettle.security.models.RefreshSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select session from RefreshSession session where session.sessionId = :sessionId")
    Optional<RefreshSession> findByIdForUpdate(@Param("sessionId") String sessionId);

    void deleteByExpiresAtBefore(Instant now);
}
