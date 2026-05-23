package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Username is required.")
    private String username;
    private String password;
}
