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

    private String ajoCode;

    private String name;

    @Enumerated(EnumType.STRING)
    private AjoStatus status;

    private BigDecimal amount;

    private String memberSlot;

    private String frequency;

    private String duration;

    @OneToOne(fetch = FetchType.LAZY)
    private Users users;
}