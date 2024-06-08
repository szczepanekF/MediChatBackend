package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.views.DoctorPatientsFromChats;

import java.util.List;

@Repository
public interface DoctorPatientsFromChatsRepository extends JpaRepository<DoctorPatientsFromChats, String> {
    List<DoctorPatientsFromChats> findAllByDoctorID(int doctorId);

}
