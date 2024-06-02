package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.DiseaseSymptom;

import java.util.List;

@Repository
public interface DiseaseSymptomRepository extends JpaRepository<DiseaseSymptom, Integer> {
    List<DiseaseSymptom> findByIdSymptom(int id);
    List<DiseaseSymptom> findByIdDisease(int id);
}
