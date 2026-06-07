package africa.royalsettle.common.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaAuditingConfigTest {

    private final AuditorAware<String> auditorAware = new JpaAuditingConfig().auditorAware();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void usesAuthenticatedUsername() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        "user@example.com",
                        null,
                        java.util.List.of()
                )
        );

        assertEquals("user@example.com", auditorAware.getCurrentAuditor().orElseThrow());
    }

    @Test
    void usesSystemForUnauthenticatedWork() {
        assertEquals("SYSTEM", auditorAware.getCurrentAuditor().orElseThrow());
    }
}
