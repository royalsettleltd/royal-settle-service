package africa.royalsettle.thrift.model;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class BankPaymentNotification {
    private String bankReference;      // Transaction reference from user/bank
    private BigDecimal amount;     // Amount paid
    private LocalDateTime timestamp; // Optional: when the bank received it

    // Getters and Setters
//    public String getReference() { return bankReference; }
//    public void setReference(String reference) { this.bankReference = bankReference; }
//
//    public BigDecimal getAmount() { return amount; }
//    public void setAmount(BigDecimal amount) { this.amount = amount; }
//
//    public LocalDateTime getTimestamp() { return timestamp; }
//    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

