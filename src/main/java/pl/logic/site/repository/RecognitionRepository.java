package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.logic.site.model.mysql.Recognition;

public interface RecognitionRepository extends JpaRepository<Recognition, Integer> {
}
