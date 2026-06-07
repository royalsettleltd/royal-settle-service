package africa.royalsettle.ajo.models;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.onboarding.models.Users;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AjoMember extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "ajo_id")
    private Ajo ajo;

    private Integer payoutPosition;

    @OneToOne
    @JoinColumn(name = "ajo_payout_id")
    private AjoPayout payout;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users users;
}
