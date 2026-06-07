package africa.royalsettle.security;

import africa.royalsettle.security.models.RefreshSession;
import africa.royalsettle.security.repository.RefreshSessionRepository;
import africa.royalsettle.security.service.RefreshSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshSessionServiceTest {

    @Mock
    private RefreshSessionRepository refreshSessionRepository;

    @InjectMocks
    private RefreshSessionService refreshSessionService;

    @Test
    void rotatesOnlyTheCurrentRefreshToken() {
        Instant expiresAt = Instant.now().plusSeconds(60);
        RefreshSession session = new RefreshSession("session", "user", "current-id", expiresAt);
        when(refreshSessionRepository.findByIdForUpdate("session")).thenReturn(Optional.of(session));

        boolean rotated = refreshSessionService.rotate(
                "session",
                "user",
                "current-id",
                "new-id",
                expiresAt.plusSeconds(60)
        );

        assertTrue(rotated);
        assertTrue(session.getRefreshTokenId().equals("new-id"));
    }

    @Test
    void rejectsReplayOfRotatedRefreshToken() {
        RefreshSession session = new RefreshSession(
                "session",
                "user",
                "new-id",
                Instant.now().plusSeconds(60)
        );
        when(refreshSessionRepository.findByIdForUpdate("session")).thenReturn(Optional.of(session));

        boolean rotated = refreshSessionService.rotate(
                "session",
                "user",
                "old-id",
                "next-id",
                Instant.now().plusSeconds(120)
        );

        assertFalse(rotated);
        verify(refreshSessionRepository).delete(session);
    }

    @Test
    void revokingSessionInvalidatesItsAccessTokens() {
        RefreshSession session = new RefreshSession(
                "session",
                "user",
                "refresh-id",
                Instant.now().plusSeconds(60)
        );
        when(refreshSessionRepository.findByIdForUpdate("session")).thenReturn(Optional.of(session));
        when(refreshSessionRepository.findById("session"))
                .thenReturn(Optional.of(session))
                .thenReturn(Optional.empty());

        assertTrue(refreshSessionService.isActive("session", "user"));
        assertTrue(refreshSessionService.revoke("session", "user", "refresh-id"));
        assertFalse(refreshSessionService.isActive("session", "user"));
        verify(refreshSessionRepository).delete(session);
    }
}
