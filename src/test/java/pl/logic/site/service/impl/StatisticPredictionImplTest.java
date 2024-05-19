package pl.logic.site.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Doctor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.logic.site.model.predictions.statictic.StatisticPrediction.getDiagnosisRequestsSizeByDaysInterval;
import static pl.logic.site.model.predictions.statictic.StatisticPrediction.getIntegerDoubleHashMap;
import static pl.logic.site.utils.predictions.PredictionConsts.MAX_DEEP_OF_PREDICTIONS;

public class StatisticPredictionImplTest {
    List<DiagnosisRequest> allDiagnosisRequests;
    List<Doctor> doctors;

    @BeforeEach
    void setUp() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date diagnosisDate1 = formatter.parse("2024-04-15 02:00:00");
        Date diagnosisDate2 = formatter.parse("2024-05-01 11:30:01");
        Date diagnosisDate3 = formatter.parse("2024-05-03 11:30:01");
        Date diagnosisDate4 = formatter.parse("2024-05-15 11:30:01");
        Date diagnosisDate5 = formatter.parse("2024-05-16 11:30:01");
        DiagnosisRequest diagnosisRequest1 = new DiagnosisRequest(1, 1, 1, "Example diagnosis", "Voice diagnosis", diagnosisDate1);
        DiagnosisRequest diagnosisRequest2 = new DiagnosisRequest(2, 2, 2, "Example diagnosis", "Voice diagnosis", diagnosisDate2);
        DiagnosisRequest diagnosisRequest3 = new DiagnosisRequest(3, 3, 3, "Example diagnosis", "Voice diagnosis", diagnosisDate3);
        DiagnosisRequest diagnosisRequest4 = new DiagnosisRequest(4, 1, 1, "Example diagnosis", "Voice diagnosis", diagnosisDate4);
        DiagnosisRequest diagnosisRequest5 = new DiagnosisRequest(5, 2, 3, "Example diagnosis", "Voice diagnosis", diagnosisDate5);
        allDiagnosisRequests = new ArrayList<>(Arrays.asList(diagnosisRequest1, diagnosisRequest2, diagnosisRequest3, diagnosisRequest4, diagnosisRequest5));

        Date birth_date_1 = formatter.parse("2002-03-01 02:00:00");
        Date birth_date_2 = formatter.parse("1998-11-25 11:30:01");
        Date birth_date_3 = formatter.parse("1974-06-11 11:30:01");
        Doctor doctor1 = new Doctor(1, "Jan", "Kowalski", birth_date_1, 1, 0);
        Doctor doctor2 = new Doctor(2, "Adam", "Nowak", birth_date_2, 1, 0);
        Doctor doctor3 = new Doctor(3, "Marek", "Kowal", birth_date_3, 1, 0);
        doctors = new ArrayList<>(Arrays.asList(doctor1, doctor2, doctor3));
    }


    @Test
    void etFutureDiagnosisRequest() throws ParseException {
        int daysInterval = 10;
        List<Integer> results = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            results.add(getDiagnosisRequestsSizeByDaysInterval(daysInterval, currentDate) * (MAX_DEEP_OF_PREDICTIONS - i + 1));
            currentDate = currentDate.minusDays(daysInterval);
        }
        int denominator = 0;
        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            denominator += i;
        }
        int meter = results.stream().mapToInt(Integer::intValue).sum();

        Double result = (double) meter / denominator;
        System.out.println(result);
        assertEquals(1.6666, result, 0.0001);
    }

    @Test
    void getMostWantedDoctor() throws ParseException {
        int daysInterval = 10;
        List<HashMap<Integer, Integer>> doctorsCounter = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();


        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            doctorsCounter.add(getDoctorsInInterval(doctors, daysInterval, currentDate));
            currentDate = currentDate.minusDays(daysInterval);
        }

        HashMap<Integer, Double> doctorsWeightAverage = getIntegerDoubleHashMap(doctorsCounter);

        Optional<Map.Entry<Integer, Double>> maxEntry = doctorsWeightAverage.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            Integer mostWantedDoctorId = maxEntry.get().getKey();
            Doctor mostWantedDoctor = findDoctorById(doctors, mostWantedDoctorId);
            System.out.println(mostWantedDoctor);
            assertEquals(3, mostWantedDoctorId);
        } else {
            throw new EntityNotFound("No doctor found");
        }
    }

    private Doctor findDoctorById(List<Doctor> doctors, int id) {
        return doctors.stream()
                .filter(doctor -> doctor.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private HashMap<Integer, Integer> getDoctorsInInterval(List<Doctor> doctors, int daysInterval, LocalDate currentDate) throws ParseException {
        List<DiagnosisRequest> diagnosis = getDiagnosisRequestsByDaysInterval(daysInterval, currentDate);
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

    private List<DiagnosisRequest> getDiagnosisRequestsByDaysInterval(int daysInterval, LocalDate currentDate) throws ParseException {
        LocalDate dateThreshold = currentDate.minusDays(daysInterval);
        List<DiagnosisRequest> allDiagnosisRequestsCopy = new ArrayList<>(allDiagnosisRequests);

        Iterator<DiagnosisRequest> iterator = allDiagnosisRequestsCopy.iterator();
        while (iterator.hasNext()) {
            DiagnosisRequest diagnosisRequest = iterator.next();
            LocalDate creationDate = diagnosisRequest.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!(creationDate.isAfter(dateThreshold) && creationDate.isBefore(currentDate))) {
                iterator.remove();
            }
        }
        return allDiagnosisRequestsCopy;
    }

    private int getDiagnosisRequestsSizeByDaysInterval(int daysInterval, LocalDate currentDate) throws ParseException {
        List<DiagnosisRequest> diagnosis = getDiagnosisRequestsByDaysInterval(daysInterval, currentDate);
//        System.out.println(diagnosis.size());
        return diagnosis.size();
    }
}
