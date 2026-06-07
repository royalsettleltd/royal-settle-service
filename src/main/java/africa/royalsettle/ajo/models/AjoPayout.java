package africa.royalsettle.ajo.models;

import africa.royalsettle.common.enums.PayoutStatus;
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
public class AjoPayout extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private AjoMember member;

    @ManyToOne
    @JoinColumn(name = "ajo_id", nullable = false)
    private Ajo ajo;

    private BigDecimal payoutAmount;

    private Integer payoutRound;

    private LocalDateTime payoutDate;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;
}
