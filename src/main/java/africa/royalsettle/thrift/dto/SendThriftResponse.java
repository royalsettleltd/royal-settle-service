package africa.royalsettle.thrift.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SendThriftResponse {
    private String message;       // Instructions for user
    private String rsReference;     // Unique reference to include in bank transfer
    private String bankName;      // Royalsettle bank name
    private String accountNumber; // Royalsettle bank account
    private String planName;      // Thrift plan name
   // private String userName;
}
