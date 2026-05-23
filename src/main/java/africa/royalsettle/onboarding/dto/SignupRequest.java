package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "fullName is required")
    @Size(max = 150, message = "fullName cannot exceed 150 characters")
    private String fullName;

    @NotBlank(message = "emailAddress is required")
    @Email(message = "emailAddress must be valid")
    @Size(max = 120, message = "emailAddress cannot exceed 120 characters")
    private String emailAddress;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phoneNumber must be 7 to 15 digits and may start with +")
    private String phoneNumber;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "referralCode is required")
    private String referralCode;
}
