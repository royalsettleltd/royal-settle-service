package africa.royalsettle.thrift.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Schema(description = "Bank-transfer instructions for a thrift contribution")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SendThriftResponse {
    @Schema(example = "Transfer the amount using the supplied reference")
    private String message;
    @Schema(example = "RS-20260607-ABC123")
    private String rsReference;
    @Schema(example = "Royal Settle Bank")
    private String bankName;
    @Schema(example = "0123456789")
    private String accountNumber;
    @Schema(example = "School Fees")
    private String planName;
}
