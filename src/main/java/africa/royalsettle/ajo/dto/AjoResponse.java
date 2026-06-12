package africa.royalsettle.ajo.dto;

import africa.royalsettle.ajo.models.Ajo;
import africa.royalsettle.common.enums.AjoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "Ajo details")
public class AjoResponse {

    @Schema(description = "Internal public identifier", example = "20260611230636000A1B2C3D4")
    private String code;

    @Schema(description = "Code used to join the Ajo", example = "RSAJ-A1B2C3")
    private String ajoCode;

    @Schema(example = "December Contribution")
    private String name;

    @Schema(example = "50000.00")
    private BigDecimal amount;

    @Schema(example = "MONTHLY")
    private String frequency;

    @Schema(example = "10 months")
    private String duration;

    @Schema(example = "PENDING")
    private AjoStatus status;

    @Schema(example = "customer@example.com")
    private String createdBy;

    public static AjoResponse toResponse(Ajo ajo) {
        return AjoResponse.builder()
                .code(ajo.getCode())
                .ajoCode(ajo.getAjoCode())
                .name(ajo.getName())
                .amount(ajo.getAmount())
                .frequency(ajo.getFrequency())
                .duration(ajo.getDuration())
                .status(ajo.getStatus())
                .createdBy(ajo.getCreatedBy())
                .build();
    }
}
