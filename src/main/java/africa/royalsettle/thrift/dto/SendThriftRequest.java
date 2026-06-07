package africa.royalsettle.thrift.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thrift contribution request")
public class SendThriftRequest {

    @Schema(example = "42")
    private Long userId;
    @Schema(example = "12")
    private Long thriftPlanId;
    @Schema(example = "25000.00")
    private BigDecimal amount;
    @Schema(example = "2026-06-07T10:30:00")
    private LocalDateTime dateTime;
}
