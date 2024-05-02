package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Recognition;

import java.util.List;

@Repository
public interface RecognitionRepository extends JpaRepository<Recognition, Integer> {
    List<Recognition> findAllByIdChart(int idchart);
}
