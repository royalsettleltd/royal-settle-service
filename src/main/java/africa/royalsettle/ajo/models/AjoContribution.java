package africa.royalsettle.ajo.models;

import africa.royalsettle.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
public class AjoContribution extends BaseEntity {

    private String ajoMemberId;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

}