package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.Ajo;
import africa.royalsettle.ajo.models.AjoMember;
import africa.royalsettle.thrift.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AjoMemberRepository extends JpaRepository<AjoMember, String> {
    // Example: find all members of a specific Ajo
    List<AjoMember> findByAjoId(String ajoId);
    Boolean existByAjoAndUser(Ajo ajo, User user);
    @Query("SELECT COUNT(m) FROM AjoMember m WHERE m.ajo = :ajo")

    long countAjoMember(Ajo ajo);
}