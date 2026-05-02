package africa.royalsettle.ajo.models;

import africa.royalsettle.common.enums.AjoStatus;
import africa.royalsettle.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Table
@Entity
@Builder
@AllArgsConstructor
public class Ajo extends BaseEntity {

    private String code;

    private String name;

    @Enumerated(EnumType.STRING)
    private AjoStatus status;

    private BigDecimal amount;

    @OneToMany(mappedBy = "ajo")
    private List<AjoMember> members;

    private String memberSlot;

    private String frequency;

    private String duration;
}