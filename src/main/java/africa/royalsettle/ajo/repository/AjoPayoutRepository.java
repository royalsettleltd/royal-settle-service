package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.AjoPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AjoPayoutRepository extends JpaRepository<AjoPayout, Long> {

}