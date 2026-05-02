package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.Ajo;
import africa.royalsettle.ajo.models.AjoMember;
import africa.royalsettle.onboarding.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AjoMemberRepository extends JpaRepository<AjoMember, String> {

    @Query("SELECT COUNT(m) FROM AjoMember m WHERE m.ajo = :ajo")
    long countAjoMember(Ajo ajo);

    boolean existsByAjoAndUsers(Ajo ajo, Users users);
}