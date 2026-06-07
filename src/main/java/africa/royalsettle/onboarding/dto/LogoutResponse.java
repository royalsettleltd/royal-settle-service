package africa.royalsettle.onboarding.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Logout result")
public class LogoutResponse {
    @Schema(example = "Logout successful")
    private String message;
}
