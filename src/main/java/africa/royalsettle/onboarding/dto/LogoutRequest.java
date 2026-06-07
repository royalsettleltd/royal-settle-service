package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Logout request for the active token session")
public class LogoutRequest {
    @NotBlank(message = "refreshToken is required")
    @Schema(description = "Refresh token belonging to the access-token session")
    private String refreshToken;
}
