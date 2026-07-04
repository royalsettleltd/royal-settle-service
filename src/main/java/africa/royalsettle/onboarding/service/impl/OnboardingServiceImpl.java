package africa.royalsettle.onboarding.service.impl;

import africa.royalsettle.common.exception.BadRequestException;
import africa.royalsettle.onboarding.dto.SetCustomerPinRequest;
import africa.royalsettle.onboarding.dto.SetCustomerPinResponse;
import africa.royalsettle.onboarding.dto.SignupRequest;
import africa.royalsettle.onboarding.dto.SignupResponse;
import africa.royalsettle.onboarding.models.UserRole;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.onboarding.enums.RoleName;
import africa.royalsettle.onboarding.repository.UserContactProjection;
import africa.royalsettle.onboarding.repository.UsersRepository;
import africa.royalsettle.onboarding.service.OnboardingService;
import africa.royalsettle.security.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;


    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String emailAddress = request.getEmailAddress().trim().toLowerCase();
        String phoneNumber = request.getPhoneNumber().trim();

        List<UserContactProjection> conflicts =
                usersRepository.findContactConflicts(emailAddress, phoneNumber);

        if (conflicts.stream().anyMatch(user -> emailAddress.equals(user.getEmailAddress()))) {
            throw new BadRequestException("emailAddress already exists");
        }

        if (conflicts.stream().anyMatch(user -> phoneNumber.equals(user.getPhoneNumber()))) {
            throw new BadRequestException("phoneNumber already exists");
        }

        Users user = Users.builder()
                .username(emailAddress)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .fullName(request.getFirstName().trim().concat(" ").concat(request.getLastName().trim()))
                .emailAddress(emailAddress)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(request.getPassword()))
                .referralCode(StringUtils.isNotBlank(request.getReferralCode()) ? request.getReferralCode().trim() : null)
                .build();

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

    @Override
    @Transactional
    public SetCustomerPinResponse setupPin(SetCustomerPinRequest request) {
        if (!request.getPin().equals(request.getConfirmPin())) {
            throw new BadRequestException("pin and confirmPin do not match");
        }

        Users currentUser = currentUserService.getCurrentUser();
        currentUser.setTransactionPin(passwordEncoder.encode(request.getPin()));
        Users savedUser = usersRepository.save(currentUser);

        return SetCustomerPinResponse.builder()
                .code(savedUser.getCode())
                .message("Transaction PIN set successfully")
                .build();
    }
}
