package africa.royalsettle.ajo.models;

import africa.royalsettle.common.enums.AjoStatus;
import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.onboarding.models.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
public class Ajo extends BaseEntity {

    @Column(nullable = false, unique = true, length = 11)
    private String ajoCode;

    private String name;

    @Enumerated(EnumType.STRING)
    private AjoStatus status;

    private BigDecimal amount;

    private String memberSlot;

    private String frequency;

    private String duration;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "initiator_id", nullable = false)
    private Users users;
}
