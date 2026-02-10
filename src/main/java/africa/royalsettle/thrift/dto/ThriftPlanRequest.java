package africa.royalsettle.thrift.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
public class ThriftPlanRequest {
    private String id;
    private String planName;
    private BigDecimal periodicAmount;
    private BigDecimal targetAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
}
