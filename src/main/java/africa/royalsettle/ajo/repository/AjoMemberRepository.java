package africa.royalsettle.ajo.repository;

import africa.royalsettle.ajo.models.Ajo;
import africa.royalsettle.ajo.models.AjoMember;
import africa.royalsettle.onboarding.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AjoMemberRepository extends JpaRepository<AjoMember, Long> {

    @Query("SELECT COUNT(m) FROM AjoMember m WHERE m.ajo = :ajo")
    long countAjoMember(Ajo ajo);

    boolean existsByAjoAndUsers(Ajo ajo, Users user);

    @Query("SELECT m.ajo FROM AjoMember m WHERE m.users = :user ORDER BY m.ajo.id DESC")
    Page<Ajo> findAjosByUser(Users user, Pageable pageable);
}
