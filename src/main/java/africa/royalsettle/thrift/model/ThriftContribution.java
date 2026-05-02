package africa.royalsettle.thrift.model;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.common.enums.ThriftContributionStatus;
import africa.royalsettle.onboarding.entity.Users;
import africa.royalsettle.transaction.model.Transaction;
import jakarta.persistence.*;
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
public class ThriftContribution extends BaseEntity {

    @Column(name = "contribution_date", nullable = false)
    private LocalDateTime contributionDate;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "thrift_plan_id")
    private ThriftPlan thriftPlan;

    @Column(name = "contribution_status")
    @Enumerated(EnumType.STRING)
    private ThriftContributionStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
}
