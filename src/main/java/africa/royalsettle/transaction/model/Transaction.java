package africa.royalsettle.transaction.model;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.common.enums.TransactionStatus;
import africa.royalsettle.common.enums.TransactionType;
import africa.royalsettle.onboarding.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private String rsReference;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private TransactionType type; // CREDIT, DEBIT, THRIFT_CONTRIBUTION, AJO_PAYOUT, PAYMENT

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 100)
    private String reference; // Optional external reference like payment ref

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private TransactionStatus status;
}

