package africa.royalsettle.ajo.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class AjoPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private AjoMember member;

    @ManyToOne
    @JoinColumn(name = "ajo_id", nullable = false)
    private Ajo ajo;

    private BigDecimal payoutAmount;

    private Integer payoutRound; // Which round in the cycle

    private LocalDateTime payoutDate;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
