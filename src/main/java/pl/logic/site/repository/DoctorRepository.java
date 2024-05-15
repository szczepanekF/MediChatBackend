package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Doctor;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    @Query(value = "SELECT d FROM Doctor d WHERE d.isBot = :doctorFilter")
    List<Doctor> retrieveDoctorsByType(@Param("doctorFilter")  int doctorFilter);

    Doctor findAllById(int id);
}
