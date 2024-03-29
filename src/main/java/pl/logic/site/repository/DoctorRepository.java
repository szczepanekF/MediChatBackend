package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
}
