package africa.royalsettle.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Customer transaction PIN setup details")
public class SetCustomerPinRequest {

    @NotBlank(message = "pin is required")
    @Pattern(regexp = "^\\d{4}$", message = "pin must be exactly 4 digits")
    @Schema(example = "1234", format = "password", minLength = 4, maxLength = 4)
    private String pin;
}
