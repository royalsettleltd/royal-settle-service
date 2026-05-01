package africa.royalsettle.thrift.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendThriftRequest {

    private String userId;
    private String thriftPlanId;
    private BigDecimal amount;
    private LocalDateTime dateTime;
}
