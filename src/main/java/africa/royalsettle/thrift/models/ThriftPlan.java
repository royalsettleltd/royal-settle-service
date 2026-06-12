package africa.royalsettle.thrift.models;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.onboarding.models.Users;
import africa.royalsettle.transaction.model.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
public class ThriftPlan extends BaseEntity {

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

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
