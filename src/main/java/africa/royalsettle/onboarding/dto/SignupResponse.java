package africa.royalsettle.onboarding.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Newly registered customer")
public class SignupResponse {
    @Schema(description = "Public customer reference", example = "20260607041825000A1B2C3D4")
    private String code;
    @Schema(example = "John Doe")
    private String fullName;
    @Schema(example = "john.doe@example.com", format = "email")
    private String emailAddress;
    @Schema(example = "+2348012345678")
    private String phoneNumber;

}
