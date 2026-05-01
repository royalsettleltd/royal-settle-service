package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.AjoPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AjoPayoutRepository extends JpaRepository<AjoPayout, String> {
    // Example: find payout by member
    Optional<AjoPayout> findByMemberId(String memberId);

    // Example: find all payouts for a specific Ajo
    List<AjoPayout> findByAjoId(String ajoId);
}