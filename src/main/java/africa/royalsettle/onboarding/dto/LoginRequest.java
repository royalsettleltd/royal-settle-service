package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {
    @Email
    private String username;
    private String password;
}
