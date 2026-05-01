package africa.royalsettle.thrift.model;
import africa.royalsettle.transaction.model.Transaction;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "thrift_plan")
public class ThriftPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "periodic_contribution", nullable = false, precision = 19, scale = 2)
    private BigDecimal periodicContribution;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "description", length = 255)
    private String description;


    // Optional relationship: A plan has many (transactions)
    @OneToMany
    private List<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;  // Owner of the plan

    @OneToMany(mappedBy = "thriftPlan", cascade = CascadeType.ALL)
    private List<ThriftContribution> contributions;
}
