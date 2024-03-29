package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
