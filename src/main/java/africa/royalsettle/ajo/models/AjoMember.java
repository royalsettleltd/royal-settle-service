package africa.royalsettle.ajo.models;

import africa.royalsettle.thrift.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
public class AjoMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "ajo_id")
    private Ajo ajo;

    // The actual user in the system
    private String userId;
    private int slotNo;
    private Integer payoutPosition;
    @OneToMany(mappedBy = "member")
    private List<AjoContribution> contributions;
    @OneToOne(mappedBy = "member")
    private AjoPayout payout;

    public void setUser(User activeUser) {
    }
}
