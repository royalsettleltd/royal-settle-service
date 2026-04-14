package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.AjoContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AjoContributionRepository extends JpaRepository<AjoContribution, String> {
    // Example: find contributions by member
    List<AjoContribution> findByMemberId(String memberId);

    // Example: find contributions by Ajo
    List<AjoContribution> findByAjoId(String ajoId);
}