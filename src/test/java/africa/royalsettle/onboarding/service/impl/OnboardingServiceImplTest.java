package africa.royalsettle.onboarding.service.impl;

import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;
import africa.royalsettle.onboarding.enums.RoleName;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.onboarding.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OnboardingServiceImpl onboardingService;

    @Test
    void createsUserWithNormalizedDetailsAndDefaultRole() {
        SignupRequest request = signupRequest();
        when(usersRepository.findByEmailAddressOrPhoneNumber(
                "user@example.com",
                "+2348012345678"
        )).thenReturn(List.of());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SignupResponse response = onboardingService.signup(request);

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(userCaptor.capture());
        Users savedUser = userCaptor.getValue();

        assertEquals("user@example.com", savedUser.getUsername());
        assertEquals("user@example.com", savedUser.getEmailAddress());
        assertEquals("+2348012345678", savedUser.getPhoneNumber());
        assertEquals("John Doe", savedUser.getFullName());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals("REF-001", savedUser.getReferralCode());
        assertTrue(savedUser.isEnabled());
        assertEquals(1, savedUser.getRoles().size());
        assertEquals(RoleName.ROYALSETTLE_USER, savedUser.getRoles().iterator().next().getName());
        assertEquals(savedUser, savedUser.getRoles().iterator().next().getUser());
        assertEquals(savedUser.getCode(), response.getCode());
        assertEquals("John Doe", response.getFullName());
    }

    @Test
    void rejectsExistingEmailFromSingleConflictLookup() {
        SignupRequest request = signupRequest();
        UsersRepository.UserContactProjection conflict = contact(
                "user@example.com",
                "+2348099999999"
        );
        when(usersRepository.findByEmailAddressOrPhoneNumber(
                "user@example.com",
                "+2348012345678"
        )).thenReturn(List.of(conflict));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> onboardingService.signup(request)
        );

        assertEquals("emailAddress already exists", exception.getMessage());
        verify(usersRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void rejectsExistingPhoneNumberFromSingleConflictLookup() {
        SignupRequest request = signupRequest();
        UsersRepository.UserContactProjection conflict = contact(
                "another@example.com",
                "+2348012345678"
        );
        when(usersRepository.findByEmailAddressOrPhoneNumber(
                "user@example.com",
                "+2348012345678"
        )).thenReturn(List.of(conflict));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> onboardingService.signup(request)
        );

        assertEquals("phoneNumber already exists", exception.getMessage());
        verify(usersRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    private SignupRequest signupRequest() {
        SignupRequest request = new SignupRequest();
        request.setFirstName(" John ");
        request.setLastName(" Doe ");
        request.setEmailAddress(" User@Example.com ");
        request.setPhoneNumber(" +2348012345678 ");
        request.setPassword("password123");
        request.setReferralCode(" REF-001 ");
        return request;
    }

    private UsersRepository.UserContactProjection contact(String emailAddress, String phoneNumber) {
        return new UsersRepository.UserContactProjection() {
            @Override
            public String getEmailAddress() {
                return emailAddress;
            }

            @Override
            public String getPhoneNumber() {
                return phoneNumber;
            }
        };
    }
}
