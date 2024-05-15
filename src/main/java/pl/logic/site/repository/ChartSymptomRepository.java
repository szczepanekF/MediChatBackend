package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.ChartSymptom;

import java.util.List;

@Repository
public interface ChartSymptomRepository extends JpaRepository<ChartSymptom, Integer> {
    List<ChartSymptom> findAllByIdChart(int idchart);
}
