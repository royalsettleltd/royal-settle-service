package africa.royalsettle.thrift.model;
import africa.royalsettle.transaction.model.Transaction;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "thrift_contribution")
public class ThriftContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
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
    private User user;
    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
}
