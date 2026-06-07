package africa.royalsettle.security.service;

import africa.royalsettle.security.models.RefreshSession;
import africa.royalsettle.security.repository.RefreshSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshSessionService {

    private final RefreshSessionRepository refreshSessionRepository;

    @Transactional
    public void create(String sessionId, String username, String refreshTokenId, Instant expiresAt) {
        refreshSessionRepository.save(
                new RefreshSession(sessionId, username, refreshTokenId, expiresAt)
        );
    }

    @Transactional(readOnly = true)
    public boolean isActive(String sessionId, String username) {
        return refreshSessionRepository.findById(sessionId)
                .filter(session -> session.getUsername().equals(username))
                .filter(session -> session.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean rotate(
            String sessionId,
            String username,
            String currentRefreshTokenId,
            String newRefreshTokenId,
            Instant newExpiresAt
    ) {
        RefreshSession session = refreshSessionRepository.findByIdForUpdate(sessionId)
                .filter(existing -> existing.getUsername().equals(username))
                .filter(existing -> existing.getExpiresAt().isAfter(Instant.now()))
                .orElse(null);

        if (session == null) {
            return false;
        }

        if (!session.getRefreshTokenId().equals(currentRefreshTokenId)) {
            refreshSessionRepository.delete(session);
            return false;
        }

        session.setRefreshTokenId(newRefreshTokenId);
        session.setExpiresAt(newExpiresAt);
        return true;
    }

    @Transactional
    public boolean revoke(String sessionId, String username, String refreshTokenId) {
        return refreshSessionRepository.findByIdForUpdate(sessionId)
                .filter(session -> session.getUsername().equals(username))
                .filter(session -> session.getRefreshTokenId().equals(refreshTokenId))
                .map(session -> {
                    refreshSessionRepository.delete(session);
                    return true;
                })
                .orElse(false);
    }

    @Scheduled(fixedDelayString = "${security.sessions.cleanup-interval:PT1H}")
    @Transactional
    public void deleteExpiredSessions() {
        refreshSessionRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
