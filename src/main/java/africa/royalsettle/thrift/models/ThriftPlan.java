package africa.royalsettle.thrift.models;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.onboarding.models.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
