package africa.royalsettle.thrift.dto;
import lombok.Data;

@Data
public class ReconcilePaymentResponse {
    private String message;
    private String transactionStatus;
    private String contributionStatus;
}

