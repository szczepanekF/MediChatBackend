package pl.logic.site.model.predictions.statictic;

import org.springframework.jdbc.core.JdbcTemplate;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.predictions.parser.SymptomParser;
import pl.logic.site.service.DiagnosisRequestService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatisticPrediction {

    /**
     * Returns the number of future diagnosis requests in the next daysInterval.
     *
     * @param jdbcTemplate - connection to the database
     * @param daysInterval - number of days interval
     * @param currentDate - current date
     * @return - number of diagnosis requests in the given days interval
     */
    public static int getDiagnosisRequestsByDaysInterval(JdbcTemplate jdbcTemplate, int daysInterval, LocalDate currentDate) {
        List<DiagnosisRequest> allDiagnosisRequests;

        String sql = "Select * from diagnosis_request;";

        allDiagnosisRequests = jdbcTemplate.query(sql, (rs, rowNum) -> {
            DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
            diagnosisRequest.setId(rs.getInt("id"));
            diagnosisRequest.setCreationDate(rs.getTimestamp("creation_date"));
            // Uzupełnij pozostałe pola obiektu DiagnosisRequest, na przykład:
            // diagnosisRequest.setSomeField(rs.getString("some_column"));
            return diagnosisRequest;
        });

        LocalDate dateThreshold = currentDate.minusDays(daysInterval);
        Iterator<DiagnosisRequest> iterator = allDiagnosisRequests.iterator();
        while (iterator.hasNext()) {
            DiagnosisRequest diagnosisRequest = iterator.next();
            LocalDate creationDate = diagnosisRequest.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!(creationDate.isAfter(dateThreshold) && creationDate.isBefore(currentDate))) {
                iterator.remove();
            }
        }
//        System.out.println(allDiagnosisRequests.size());
        return allDiagnosisRequests.size();
    }
}
