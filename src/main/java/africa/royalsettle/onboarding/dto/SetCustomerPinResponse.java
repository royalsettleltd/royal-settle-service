package africa.royalsettle.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Customer PIN setup result")
public class SetCustomerPinResponse {

    @Schema(description = "Public customer reference", example = "20260607041825000A1B2C3D4")
    private String code;

    @Schema(example = "Transaction PIN set successfully")
    private String message;
}
