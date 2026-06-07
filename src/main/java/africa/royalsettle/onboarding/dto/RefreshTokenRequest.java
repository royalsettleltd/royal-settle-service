package africa.royalsettle.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Refresh-token rotation request")
public class RefreshTokenRequest {

    @NotBlank(message = "refreshToken is required")
    @Schema(description = "Valid, unexpired refresh token")
    private String refreshToken;
}
