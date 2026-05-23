package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.AjoContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AjoContributionRepository extends JpaRepository<AjoContribution, Long> {

}