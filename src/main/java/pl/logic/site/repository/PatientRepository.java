package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.mysql.Patient;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findAllByStatus(Status status);
    Patient findByName(String name);
    Patient findByNameAndAndSurname(String name, String surname);
}
