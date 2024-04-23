package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Chart;

import java.util.List;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Integer> {
    List<Chart> findByIdPatient(int id);
}
