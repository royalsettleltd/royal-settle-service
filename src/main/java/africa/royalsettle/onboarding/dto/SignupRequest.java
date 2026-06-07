package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "firstName is required")
    @Size(max = 75, message = "firstName cannot exceed 75 characters")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Size(max = 75, message = "lastName cannot exceed 75 characters")
    private String lastName;

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

    private String referralCode;
}
