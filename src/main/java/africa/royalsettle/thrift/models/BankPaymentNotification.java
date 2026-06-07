package africa.royalsettle.thrift.models;

import africa.royalsettle.common.dto.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Incoming bank payment notification")
public class BankPaymentNotification extends BaseEntity {
    @Schema(description = "Reference supplied with the bank transfer", example = "RS-20260607-ABC123")
    private String bankReference;      // Transaction reference from user/bank
    @Schema(example = "25000.00")
    private BigDecimal amount;     // Amount paid
    @Schema(example = "2026-06-07T10:30:00")
    private LocalDateTime timestamp; // Optional: when the bank received it
}
