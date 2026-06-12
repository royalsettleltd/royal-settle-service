package africa.royalsettle.onboarding.repository;

import africa.royalsettle.onboarding.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);

    @Query("""
            select user.emailAddress as emailAddress, user.phoneNumber as phoneNumber
            from Users user
            where user.emailAddress = :emailAddress or user.phoneNumber = :phoneNumber
            """)
    List<UserContactProjection> findContactConflicts(
            @Param("emailAddress") String emailAddress,
            @Param("phoneNumber") String phoneNumber
    );

    Optional<Users> findByCode(String userCode);
}
