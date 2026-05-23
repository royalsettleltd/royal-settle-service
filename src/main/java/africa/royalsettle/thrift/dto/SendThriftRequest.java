package africa.royalsettle.thrift.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendThriftRequest {

    private Long userId;
    private Long thriftPlanId;
    private BigDecimal amount;
    private LocalDateTime dateTime;
}
