package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.DiagnosisRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRequestRepository extends JpaRepository<DiagnosisRequest, Integer> {
    List<DiagnosisRequest> findAllByIdChart(int idchart);
    Optional<DiagnosisRequest> findAllById(int id);
    Optional<DiagnosisRequest> findById(int id);
}
