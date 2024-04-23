package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.logic.site.model.mysql.SpringUser;

import java.util.List;
import java.util.Optional;

public interface SpringUserRepository extends JpaRepository<SpringUser, Integer> {
    Optional<SpringUser> findByEmail(String email);
    Optional<SpringUser> findByUsername(String username);
    Optional<SpringUser> findByUsernameOrEmail(String username, String email);
    Optional<SpringUser> findByPatientId(int patientId);
    Optional<SpringUser> findByDoctorId(int doctorId);

    List<Optional<SpringUser>> findAllByPatientIdNotNull();
    List<Optional<SpringUser>> findAllByDoctorIdNotNull();
    @Query(value = "SELECT k FROm SpringUser k")
    List<Optional<SpringUser>> retrieveAll();

}
