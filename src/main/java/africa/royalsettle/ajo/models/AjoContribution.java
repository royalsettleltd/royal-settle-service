package africa.royalsettle.ajo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class AjoContribution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String ajoMemberId;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

}