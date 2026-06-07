package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Customer login credentials")
public class LoginRequest {

    @NotBlank(message = "Username is required.")
    @Email(message = "Username must be a valid email address.")
    @Schema(description = "Customer email address", example = "user@example.com")
    private String username;

    @NotBlank(message = "Password is required.")
    @Schema(description = "Customer password", example = "StrongPassword123!", format = "password")
    private String password;
}
