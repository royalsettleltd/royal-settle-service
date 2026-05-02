package africa.royalsettle.onboarding.repository;

import africa.royalsettle.onboarding.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    Optional<Users> findByUsername(String username);
}
