package africa.royalsettle.ajo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Schema(description = "Ajo creation request")
public class CreateAjoRequest {

    @NotBlank(message = "name is required")
    @Schema(example = "December Contribution")
    private String name;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    @Schema(example = "50000.00")
    private BigDecimal amount;

    @NotBlank(message = "frequency is required")
    @Schema(example = "MONTHLY")
    private String frequency;

    @NotBlank(message = "duration is required")
    @Schema(example = "10 months")
    private String duration;
}
