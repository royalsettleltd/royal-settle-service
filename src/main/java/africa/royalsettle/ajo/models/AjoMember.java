package africa.royalsettle.ajo.models;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.onboarding.models.Users;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_ajo_member_ajo_user",
        columnNames = {"ajo_id", "user_id"}
))
@Entity
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AjoMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ajo_id", nullable = false)
    private Ajo ajo;

    private Integer payoutPosition;

    @OneToOne
    @JoinColumn(name = "ajo_payout_id")
    private AjoPayout payout;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;
}
