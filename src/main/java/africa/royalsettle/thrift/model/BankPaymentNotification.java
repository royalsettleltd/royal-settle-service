package africa.royalsettle.thrift.model;

import africa.royalsettle.common.dto.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BankPaymentNotification extends BaseEntity {
    private String bankReference;      // Transaction reference from user/bank
    private BigDecimal amount;     // Amount paid
    private LocalDateTime timestamp; // Optional: when the bank received it
}

