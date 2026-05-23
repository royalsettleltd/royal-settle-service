package africa.royalsettle.onboarding.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponse {
    private String code;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String referralCode;
}
