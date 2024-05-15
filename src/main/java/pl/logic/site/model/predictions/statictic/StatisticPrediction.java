package pl.logic.site.model.predictions.statictic;

import org.springframework.jdbc.core.JdbcTemplate;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Doctor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pl.logic.site.utils.predictions.PredictionConsts.MAX_DEEP_OF_PREDICTIONS;

public class StatisticPrediction {

    /**
     * Returns the number of future diagnosis requests in the next daysInterval.
     *
     * @param jdbcTemplate - connection to the database
     * @param daysInterval - number of days interval
     * @param currentDate  - current date
     * @return - number of diagnosis requests in the given days interval
     */
    public static int getDiagnosisRequestsSizeByDaysInterval(JdbcTemplate jdbcTemplate, int daysInterval, LocalDate currentDate) {
        List<DiagnosisRequest> diagnosis = getDiagnosisRequestsByDaysInterval(jdbcTemplate, daysInterval, currentDate);
//        System.out.println(diagnosis.size());
        return diagnosis.size();
    }

    public static HashMap<Integer, Integer> getDoctorsInInterval(JdbcTemplate jdbcTemplate, List<Doctor> doctors, int daysInterval, LocalDate currentDate) {
        List<DiagnosisRequest> diagnosis = getDiagnosisRequestsByDaysInterval(jdbcTemplate, daysInterval, currentDate);
        HashMap<Integer, Integer> doctorsInInterval = new HashMap<>();

        for (Doctor doctor : doctors) {
            doctorsInInterval.put(doctor.getId(), 0);
        }

        for (DiagnosisRequest diagnose : diagnosis) {
            for (Map.Entry<Integer, Integer> entry : doctorsInInterval.entrySet()) {
                Integer doctorId = entry.getKey();
                Integer count = entry.getValue();

                if (diagnose.getIdDoctor() == doctorId) {
                    count++;
                    doctorsInInterval.put(doctorId, count);
                }
            }
        }
//        System.out.println(doctorsInInterval);
        return doctorsInInterval;
    }

    public static HashMap<Integer, Double> getIntegerDoubleHashMap(List<HashMap<Integer, Integer>> doctorsCounter) {
        HashMap<Integer, Integer> meter = getIntegerIntegerHashMap(doctorsCounter);

        int denominator = 0;
        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            denominator += i;
        }

        HashMap<Integer, Double> doctorsWeightAverage = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : meter.entrySet()) {
            Integer doctorId = entry.getKey();
            Integer count = entry.getValue();
            doctorsWeightAverage.put(doctorId, (double) count / denominator);
        }
        return doctorsWeightAverage;
    }

    private static HashMap<Integer, Integer> getIntegerIntegerHashMap(List<HashMap<Integer, Integer>> doctorsCounter) {
        HashMap<Integer, Integer> meter = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : doctorsCounter.getFirst().entrySet()) {
            Integer doctorId = entry.getKey();
            meter.put(doctorId, 0);
        }

        for (int i = 0; i < doctorsCounter.size(); i++) {
            for (Map.Entry<Integer, Integer> entry : doctorsCounter.get(i).entrySet()) {
                Integer doctorId = entry.getKey();
                Integer count = entry.getValue();
                Integer temp = meter.get(doctorId);
                meter.put(doctorId, temp + count * (MAX_DEEP_OF_PREDICTIONS - i + 1));
            }
        }
        return meter;
    }

    private static List<DiagnosisRequest> getDiagnosisRequestsByDaysInterval(JdbcTemplate jdbcTemplate, int daysInterval, LocalDate currentDate) {
        List<DiagnosisRequest> allDiagnosisRequests;
        String sql = "Select * from diagnosis_request;";

        allDiagnosisRequests = jdbcTemplate.query(sql, (rs, rowNum) -> {
            DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
            diagnosisRequest.setId(rs.getInt("id"));
            diagnosisRequest.setIdDoctor(rs.getInt("id_doctor"));
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
        return allDiagnosisRequests;
    }
}
