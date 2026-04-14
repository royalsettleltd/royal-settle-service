package africa.royalsettle.ajo.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Ajo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String code;
    private String name;

    @Enumerated(EnumType.STRING)
    private AjoStatus status;

    private BigDecimal amount;

    public static final int MAX_AJO_SLOT = 10;
    public static final int MIN_AJO_SLOT = 3;

    @OneToMany(mappedBy = "ajo")
    private List<AjoMember> members;

    private String memberSlot;

    private String frequency;

    private String duration;

    private String createdBy;

    private LocalDateTime createdAt;

}
