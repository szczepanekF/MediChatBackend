package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.logic.site.model.mysql.SpringUser;

import java.util.Optional;

public interface SpringUserRepository extends JpaRepository<SpringUser, Integer> {
    Optional<SpringUser> findByEmail(String email);
    Optional<SpringUser> findByUsername(String username);
    Optional<SpringUser> findByUsernameOrEmail(String username, String email);
}
