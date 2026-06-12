package africa.royalsettle.thrift.dto;

import africa.royalsettle.thrift.models.ThriftPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Thrift plan details")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ThriftPlanResponse {

    @Schema(example = "12")
    private String planCode;
    @Schema(example = "School Fees")
    private String planName;
    @Schema(example = "25000.00")
    private BigDecimal periodicAmount;
    @Schema(example = "300000.00")
    private BigDecimal targetAmount;
    @Schema(example = "John Doe")
    private String userFullName;
    @Schema(example = "2026-07-01T00:00:00")
    private LocalDate startDate;
    @Schema(example = "2027-06-30T23:59:59")
    private LocalDate endDate;
    @Schema(example = "Monthly savings for school fees")
    private String description;
    @Schema(example = "false")
    private boolean isCompleted;


    public static ThriftPlanResponse mapToResponse(ThriftPlan plan) {
        return ThriftPlanResponse.builder()
                .planCode(plan.getCode())
                .planName(plan.getPlanName())
                .description(plan.getDescription())
                .userFullName(plan.getUser().getFullName())
                .periodicAmount(plan.getPeriodicContribution())
                .targetAmount(plan.getTargetAmount())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .isCompleted(plan.getIsCompleted())
                .build();
    }
}
