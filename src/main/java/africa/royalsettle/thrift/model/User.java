package africa.royalsettle.thrift.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Column(name = "first_name", nullable = false, length = 50)

    private String firstName;
    @Column(name = "last_name", nullable = false, length = 50)

    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private Wallet wallet;


    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
