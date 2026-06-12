package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.Ajo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AjoRepository extends JpaRepository<Ajo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Ajo a WHERE a.ajoCode = :ajoCode")
    Optional<Ajo> findByAjoCodeForUpdate(String ajoCode);
}
