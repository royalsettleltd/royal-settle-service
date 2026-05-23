package africa.royalsettle.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    public static final String JWT_REQUEST_ATTRIBUTE = JwtAuthenticationFilter.class.getName() + ".JWT";
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            extractJwtFromRequest(request)
                    .filter(StringUtils::hasText)
                    .filter(jwt -> !tokenBlacklistService.isBlacklisted(jwt))
                    .filter(jwtTokenUtil::validateToken)
                    .filter(jwtTokenUtil::isAccessToken)
                    .ifPresent(jwt -> {
                        String username = jwtTokenUtil.extractUsername(jwt);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        var auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        request.setAttribute(JWT_REQUEST_ATTRIBUTE, jwt);
                    });
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractJwtFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(h -> h.startsWith(BEARER))
                .map(h -> h.substring(BEARER.length()));
    }
}
