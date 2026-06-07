package africa.royalsettle.security;

import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.onboarding.repository.UsersRepository;
import africa.royalsettle.security.dto.AppUserPrincipal;
import africa.royalsettle.security.service.CurrentUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsAuthenticatedUser() {
        Users user = Users.builder().username("current-user").build();
        user.setId(42L);
        UserDetails principal = new AppUserPrincipal(
                42L,
                "current-user",
                "password",
                AuthorityUtils.NO_AUTHORITIES,
                true,
                true,
                true,
                true
        );
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal,
                null,
                AuthorityUtils.NO_AUTHORITIES
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(usersRepository.findById(42L)).thenReturn(Optional.of(user));

        Users result = currentUserService.getCurrentUser();

        assertSame(user, result);
        verify(usersRepository).findById(42L);
    }

    @Test
    void rejectsMissingAuthentication() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                currentUserService::getCurrentUser
        );
    }

    @Test
    void rejectsAnonymousAuthentication() {
        var authentication = new AnonymousAuthenticationToken(
                "anonymous-key",
                "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                currentUserService::getCurrentUser
        );
    }

    @Test
    void rejectsAuthenticatedUserMissingFromDatabase() {
        UserDetails principal = new AppUserPrincipal(
                99L,
                "deleted-user",
                "password",
                AuthorityUtils.NO_AUTHORITIES,
                true,
                true,
                true,
                true
        );
        var authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal,
                null,
                AuthorityUtils.NO_AUTHORITIES
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, currentUserService::getCurrentUser);
    }
}
