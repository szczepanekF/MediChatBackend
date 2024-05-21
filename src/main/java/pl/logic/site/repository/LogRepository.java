package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Log;
import pl.logic.site.model.mysql.Room;

import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
