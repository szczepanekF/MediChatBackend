package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.logic.site.model.mysql.PasswordResetToken;

import java.util.Optional;

public interface PasswordRecoveryTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByRecoveryToken(String token);
}
