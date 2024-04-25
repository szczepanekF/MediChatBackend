package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.DictionaryExamination;

@Repository
public interface DictionaryExaminationRepository  extends JpaRepository<DictionaryExamination, Integer> {
}
