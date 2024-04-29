package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Examination;

import java.util.List;


@Repository
public interface ExaminationRepository  extends JpaRepository<Examination, Integer> {
    List<Examination> findAllByIdPatient(int examinationFilter);
}
