package africa.royalsettle.onboarding.service.impl;

import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;
import africa.royalsettle.onboarding.models.UserRole;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.onboarding.enums.RoleName;
import africa.royalsettle.onboarding.repository.UsersRepository;
import africa.royalsettle.onboarding.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String emailAddress = request.getEmailAddress().trim().toLowerCase();
        String phoneNumber = request.getPhoneNumber().trim();

        if (usersRepository.existsByEmailAddress(emailAddress)) {
            throw new IllegalArgumentException("emailAddress already exists");
        }

        if (usersRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("phoneNumber already exists");
        }

        Users user = new Users();
        user.setUsername(emailAddress);
        user.setFullName(request.getFullName().trim());
        user.setEmailAddress(emailAddress);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setReferralCode(request.getReferralCode());

        UserRole role = UserRole.builder()
                .name(RoleName.ROYALSETTLE_USER)
                .user(user)
                .build();
        user.getRoles().add(role);

        Users savedUser = usersRepository.save(user);

        return SignupResponse.builder()
                .code(savedUser.getCode())
                .fullName(savedUser.getFullName())
                .emailAddress(savedUser.getEmailAddress())
                .phoneNumber(savedUser.getPhoneNumber())
                .referralCode(savedUser.getReferralCode())
                .build();
    }
}
