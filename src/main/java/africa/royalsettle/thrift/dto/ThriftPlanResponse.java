package africa.royalsettle.thrift.dto;

import africa.royalsettle.thrift.models.ThriftPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Thrift plan details")
public class ThriftPlanResponse {

    @Schema(example = "12")
    private Long planId;
    @Schema(example = "School Fees")
    private String planName;
    @Schema(example = "25000.00")
    private BigDecimal periodicAmount;
    @Schema(example = "300000.00")
    private BigDecimal targetAmount;
    @Schema(example = "John Doe")
    private String userFullName;
    @Schema(example = "2026-07-01T00:00:00")
    private LocalDateTime startDate;
    @Schema(example = "2027-06-30T23:59:59")
    private LocalDateTime endDate;
    @Schema(example = "Monthly savings for school fees")
    private String description;
    @Schema(example = "false")
    private boolean isCompleted;


    public static ThriftPlanResponse mapToResponse(ThriftPlan plan) {
        ThriftPlanResponse response = new ThriftPlanResponse();
        response.setPlanId(plan.getId());
        response.setPlanName(plan.getPlanName());
        response.setDescription(plan.getDescription());

        response.setUserFullName(plan.getUser().getFullName());
        response.setPeriodicAmount(plan.getPeriodicContribution());
        response.setTargetAmount(plan.getTargetAmount());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setCompleted(plan.getIsCompleted());

        return response;
    }

}
