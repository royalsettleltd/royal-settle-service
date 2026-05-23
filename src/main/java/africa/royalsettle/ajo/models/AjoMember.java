package africa.royalsettle.ajo.models;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.onboarding.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AjoMember extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ajo_id")
    private Ajo ajo;

    private int slotNo;

    private Integer payoutPosition;

    @OneToOne
    @JoinColumn(name = "ajo_payout_id")
    private AjoPayout payout;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;
}
