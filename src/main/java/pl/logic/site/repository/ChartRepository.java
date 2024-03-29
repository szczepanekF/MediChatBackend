package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Chart;

@Repository
public interface ChartRepository extends JpaRepository<Chart, Long> {
}
