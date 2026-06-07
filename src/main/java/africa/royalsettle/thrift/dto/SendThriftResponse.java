package africa.royalsettle.thrift.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Bank-transfer instructions for a thrift contribution")

public class SendThriftResponse {
    @Schema(example = "Transfer the amount using the supplied reference")
    private String message;       // Instructions for user
    @Schema(example = "RS-20260607-ABC123")
    private String rsReference;     // Unique reference to include in bank transfer
    @Schema(example = "Royal Settle Bank")
    private String bankName;      // Royalsettle bank name
    @Schema(example = "0123456789")
    private String accountNumber; // Royalsettle bank account
    @Schema(example = "School Fees")
    private String planName;      // Thrift plan name
   // private String userName;
}
