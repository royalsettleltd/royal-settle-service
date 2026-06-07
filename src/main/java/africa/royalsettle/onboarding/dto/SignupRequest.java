package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Customer account registration details")
public class SignupRequest {

    @NotBlank(message = "firstName is required")
    @Size(max = 75, message = "firstName cannot exceed 75 characters")
    @Schema(example = "John", maxLength = 75)
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Size(max = 75, message = "lastName cannot exceed 75 characters")
    @Schema(example = "Doe", maxLength = 75)
    private String lastName;

    @NotBlank(message = "emailAddress is required")
    @Email(message = "emailAddress must be valid")
    @Size(max = 120, message = "emailAddress cannot exceed 120 characters")
    @Schema(example = "john.doe@example.com", format = "email", maxLength = 120)
    private String emailAddress;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phoneNumber must be 7 to 15 digits and may start with +")
    @Schema(example = "+2348012345678", pattern = "^\\+?[0-9]{7,15}$")
    private String phoneNumber;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters")
    @Schema(example = "StrongPassword123!", format = "password", minLength = 8, maxLength = 100)
    private String password;

    @Schema(description = "Optional referral code", example = "REF-001", nullable = true)
    private String referralCode;
}
