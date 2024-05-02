package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.mysql.SymptomValues;


@Repository
public interface SymptomValuesRepository extends JpaRepository<SymptomValues, Integer> {
}
