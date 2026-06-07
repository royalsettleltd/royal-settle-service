package africa.royalsettle.security;

import africa.royalsettle.security.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private User userDetails;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(
                jwtTokenUtil,
                "secret",
                "test-secret-that-is-at-least-thirty-two-bytes-long"
        );
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 900_000L);
        ReflectionTestUtils.setField(jwtTokenUtil, "refreshExpiration", 604_800_000L);
        userDetails = new User(
                "user@example.com",
                "password",
                AuthorityUtils.createAuthorityList("ROYALSETTLE_USER")
        );
    }

    @Test
    void generatesStrictAccessTokenWithTokenId() {
        String token = jwtTokenUtil.generateToken(userDetails, "session-id");

        assertTrue(jwtTokenUtil.validateToken(token, userDetails));
        assertTrue(jwtTokenUtil.isAccessToken(token));
        assertFalse(jwtTokenUtil.isRefreshToken(token));
        assertNotNull(jwtTokenUtil.extractTokenId(token));
        assertTrue("session-id".equals(jwtTokenUtil.extractSessionId(token)));
    }

    @Test
    void generatesStrictRefreshTokenWithTokenId() {
        String token = jwtTokenUtil.generateRefreshToken(userDetails, "session-id");

        assertTrue(jwtTokenUtil.validateToken(token, userDetails));
        assertTrue(jwtTokenUtil.isRefreshToken(token));
        assertFalse(jwtTokenUtil.isAccessToken(token));
        assertNotNull(jwtTokenUtil.extractTokenId(token));
    }

    @Test
    void rejectsTokenWithoutTypeAndSessionAsAccessToken() {
        String legacyToken = ReflectionTestUtils.invokeMethod(
                jwtTokenUtil,
                "createToken",
                Map.of(),
                userDetails.getUsername(),
                900_000L
        );

        assertFalse(jwtTokenUtil.isAccessToken(legacyToken));
    }
}
