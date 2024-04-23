package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Recognition;
import pl.logic.site.model.mysql.Specialisation;

@Repository
public interface SpecialisationRepository extends JpaRepository<Specialisation, Integer> {
}
