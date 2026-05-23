package africa.royalsettle.onboarding.entity;

import africa.royalsettle.common.dto.BaseEntity;
import africa.royalsettle.onboarding.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoleName name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
