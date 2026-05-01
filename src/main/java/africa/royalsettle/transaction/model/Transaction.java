package africa.royalsettle.transaction.model;
import africa.royalsettle.thrift.model.User;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

//    @ManyToOne
//    @JoinColumn(name = "wallet_id", nullable = false)
//    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String rsReference;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private TransactionType type; // CREDIT, DEBIT, THRIFT_CONTRIBUTION, AJO_PAYOUT, PAYMENT

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 100)
    private String reference; // Optional external reference like payment ref

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

