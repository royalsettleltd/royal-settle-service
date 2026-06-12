package africa.royalsettle.thrift.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Thrift plan creation request")
public class ThriftPlanRequest {

    @Schema(example = "School Fees")
    private String planName;
    @Schema(example = "25000.00")
    private BigDecimal periodicAmount;
    @Schema(example = "300000.00")
    private BigDecimal targetAmount;
    @Schema(example = "2026-07-01")
    private LocalDate startDate;
    @Schema(example = "2027-06-30")
    private LocalDate endDate;
    @Schema(example = "Monthly savings for school fees")
    private String description;
}
