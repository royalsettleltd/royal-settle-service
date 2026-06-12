package africa.royalsettle.thrift.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Bank payment reconciliation result")
public class ReconcilePaymentResponse {
    @Schema(example = "Payment reconciled successfully")
    private String message;
    @Schema(example = "SUCCESSFUL")
    private String transactionStatus;
    @Schema(example = "CONFIRMED")
    private String contributionStatus;
}
