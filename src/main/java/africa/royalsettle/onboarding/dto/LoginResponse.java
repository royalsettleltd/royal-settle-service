package africa.royalsettle.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Issued JWT token pair")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    @Schema(description = "Authenticated username", example = "user@example.com")
    private String username;
    @Schema(description = "Short-lived JWT access token")
    private String accessToken;
    @Schema(description = "Rotating JWT refresh token")
    private String refreshToken;
    @Schema(description = "Authorization scheme", example = "Bearer")
    private String tokenType;
    @Schema(description = "Authenticated customer profile")
    private UserDetailsResponse userDetails;
}
