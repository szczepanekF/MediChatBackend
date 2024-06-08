package pl.logic.site.service.impl;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.logic.site.model.enums.ReportFiletype;
import pl.logic.site.model.enums.ReportType;
import pl.logic.site.model.mysql.*;
import pl.logic.site.model.reportsForms.ReportCreateForm;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.repository.SymptomRepository;
import pl.logic.site.service.StatisticsService;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final String[] ageGroups = {"0-5", "6-10", "11-18", "19-30", "31-50", "51-70", "71+"};
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SymptomRepository symptomRepository;

    public StatisticsServiceImpl(DoctorRepository doctorRepository, PatientRepository patientRepository, SymptomRepository symptomRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.symptomRepository = symptomRepository;
    }

    @Override
    @Transactional
    public Report createReport(ReportCreateForm reportCreateForm) {
        Report report = new Report();

        report.setTitle(reportCreateForm.getTitle());

        String pdf = extractReportPDF(reportCreateForm);



        return report;
    }

    private String extractReportPDF(ReportCreateForm reportCreateForm){
        if(reportCreateForm.getFiletype() == ReportFiletype.pdf){
            return switch (reportCreateForm.getReportType()){
                case ReportType.user -> createUserReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases -> createDiseasesReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case symptoms_date -> null;
                case diseases_date -> null;
//                case symptoms_doctor -> null;
//                case diseases_doctor -> null;
                case symptoms_age_groups -> null;
                case diseases_age_groups -> null;
                case age_groups -> null;
                case new_users -> null;
            };
        } else if(reportCreateForm.getFiletype() == ReportFiletype.csv){
            return switch (reportCreateForm.getReportType()){
                case user -> null;
                case diseases -> null;
                case ReportType.symptoms_date -> createSymptomsDateReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases_date -> createDiseasesDateReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
//                case ReportType.symptoms_doctor -> createSymptomsDoctorReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
//                case ReportType.diseases_doctor -> createDiseasesDoctorReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.symptoms_age_groups -> createSymptomsAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases_age_groups -> createDiseasesAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.age_groups -> createAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.new_users -> createNewUsersReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
            };
        }
        return null;
    }

    private String createPDF(int idDoctor, int from, int to, String content) {
        return "";//return encoded file
    }

    private String createUserReport(int idDoctor, Date fromDate, Date toDate){
        Doctor doctor = doctorRepository.findAllById(idDoctor);
        List<String> messagesPart = getMessagesPart(doctor, fromDate, toDate);
        List<String> diagnosisRequestsPart = getDiagnosisRequestsPart(doctor, fromDate, toDate);
        List<List<String>> newPatientsPart = getNewPatientsPart(doctor, fromDate, toDate);
        List<List<String>> patientAgeGroups = getPatientAgeGroups(doctor);
        //create bar chart with 2 series for all patients and my patients: darkblue and blue from front (btn-default but just rgb)
        List<List<Object>> symptomAgeGroupsPart = createSymptomAgeGroups(idDoctor, fromDate, toDate, "pdf");

        return "";
    }

    private List<String> getMessagesPart(Doctor doctor, Date fromDate, Date toDate){
        List<String> messagesPart = new ArrayList<>();
        int answered = 0;
        int received = 0;
        //get doctor chats
        //foreach chat get messages from timespan
        //if message recipient is doctor add to received else to answered
        messagesPart.add(String.valueOf(answered));
        messagesPart.add(String.valueOf(received));
        return messagesPart;
    }

    private List<String> getDiagnosisRequestsPart(Doctor doctor, Date fromDate, Date toDate){
        List<String> diagnosisRequestsPart = new ArrayList<>();
        int diagnosed = 0;
        int received = 0;
        //get doctor diagnosis requests for given time
        //count all as received
        //count all having diagnosis as diagnosed
        diagnosisRequestsPart.add(String.valueOf(diagnosed));
        diagnosisRequestsPart.add(String.valueOf(received));
        return diagnosisRequestsPart;
    }

    private List<List<String>> getNewPatientsPart(Doctor doctor, Date fromDate, Date toDate){
        List<List<String>> newPatients = new ArrayList<>();
        //fetch new patients for doctor in timeframe
        //foreach patient chosen create row with name surname, chat creation date, if started with diagnosis request
        return newPatients;
    }

    private List<List<String>> getPatientAgeGroups(Doctor doctor){
        List<Patient> allPatients = patientRepository.findAll();
        List<Integer> all_patients_age_groups = new ArrayList<>();
        for(int i = 0; i < ageGroups.length; i++){
            all_patients_age_groups.add(0);
        }
        List<Integer> my_patients_age_groups = new ArrayList<>();
        for(int i = 0; i < ageGroups.length; i++){
            my_patients_age_groups.add(0);
        }

        //foreach patient check its age group + add count there for 1st list
        //if doctor.getMyPatients().stream().anyMatch(item -> item.equals(patient)) then count it in same age group as my patient

        List<String> res1 = new ArrayList<>();
        List<String> res2 = new ArrayList<>();
        for(int i = 0; i < ageGroups.length; i++){
            res1.add(all_patients_age_groups.get(i).toString());
        }
        for(int i = 0; i < ageGroups.length; i++){
            res2.add(my_patients_age_groups.get(i).toString());
        }
        List<List<String>> patientAgeGroups = new ArrayList<>();
        patientAgeGroups.add(res1);
        patientAgeGroups.add(res2);

        return patientAgeGroups;
    }

    private String createDiseasesReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createSymptomsDateReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createDiseasesDateReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

//    private String createSymptomsDoctorReport(int idDoctor, Date fromDate, Date toDate){
//        return "";
//    }
//
//    private String createDiseasesDoctorReport(int idDoctor, Date fromDate, Date toDate){
//        return "";
//    }

    private String createSymptomsAgeGroupsReport(int idDoctor, Date fromDate, Date toDate){
        List<List<Object>> symptomAgeGroups = createSymptomAgeGroups(idDoctor, fromDate, toDate, "csv");
        //create file
        return "";
    }

    private List<List<Object>> createSymptomAgeGroups(int idDoctor, Date fromDate, Date toDate, String filetype){
        List<Symptom> symptoms = symptomRepository.findAll();
        List<String> symptomNames = new ArrayList<>();
        for(int i = 0; i < symptoms.size(); i++){
            symptomNames.add(symptoms.get(i).getName());
        }
        List<List<Integer>> data = new ArrayList<>();
        //init data table
        for(int i = 0; i < ageGroups.length; i++){
            data.add(new ArrayList<>());
            for(int j = 0; j < symptoms.size(); j++){
                data.get(i).add(0);
            }
        }

        List<List<Object>> result = new ArrayList<>();
        if(Objects.equals(filetype, "pdf")){
            result.add(new ArrayList<>());
            result.get(0).add("Age groups");
            for(int j=0; j < symptomNames.size(); j++){
                result.get(0).add(symptomNames.get(j));
            }
            for(int i=1; i <= ageGroups.length; i++){
                result.get(i).add(ageGroups[i-1]);
                for(int j=1; j <= symptomNames.size(); j++){
                    result.get(i).add(data.get(i).get(j));
                }
            }
            return result;
        } else if(Objects.equals(filetype, "csv")) {

            List<Object> csvData = new ArrayList<>();
            csvData.addAll(symptomNames);
            csvData.addAll(Arrays.asList(ageGroups));
            for (List<Integer> rowData : data) {
                csvData.addAll(rowData);
            }
            result.add(csvData);
            return result;
//            result.add(symptomNames);
//            result.add(ageGroups);
//            result.add(data);
//            return result;
        } else {
            throw new InvalidParameterException("Invalid filetype");
        }
    }

    private String createDiseasesAgeGroupsReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createAgeGroupsReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createNewUsersReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }


    public static int findSymptomIndex(List<Symptom> symptomsList, Symptom symptom) {
        for (int i = 0; i < symptomsList.size(); i++) {
            if (symptomsList.get(i).getId() == symptom.getId()) {
                return i;
            }
        }
        return -1; // Symptom not found in the list
    }

    public static int findDiseaseIndex(List<Disease> diseasesList, Disease disease) {
        for (int i = 0; i < diseasesList.size(); i++) {
            if (diseasesList.get(i).getId() == disease.getId()) {
                return i;
            }
        }
        return -1; // Disease not found in the list
    }

    public int findAgeGroupIndex(int age) {
        for (int i = 0; i < ageGroups.length; i++) {
            String ageGroup = ageGroups[i];
            if (ageGroup.endsWith("+")) {
                // This is the last group "71+"
                int lowerBound = Integer.parseInt(ageGroup.substring(0, ageGroup.length() - 1));
                if (age >= lowerBound) {
                    return i;
                }
            } else {
                String[] bounds = ageGroup.split("-");
                int lowerBound = Integer.parseInt(bounds[0]);
                int upperBound = Integer.parseInt(bounds[1]);
                if (age >= lowerBound && age <= upperBound) {
                    return i;
                }
            }
        }
        // If no group is found (should not happen with valid input)
        throw new IllegalArgumentException("Age out of range");
    }

    /**
     * For given time range (from - to) returns list of converted dates to correct format for tables:
     *      - days type if between from and to is up to 2 months
     *      - months type if between from and to is over 2 months
     * @param fromDate - start date
     * @param toDate - end date
     *
     * @return List<String>
     * @throws IllegalArgumentException - invalid dates configuration
     * */
    public static List<String> generateDateRange(Date fromDate, Date toDate) {
        LocalDate from = convertToLocalDate(fromDate);
        LocalDate to = convertToLocalDate(toDate);

        List<String> timeList = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }

        // Determine whether to use days or months
        if (from.plusMonths(2).isBefore(to) || from.plusMonths(2).isEqual(to)) {
            // Over 2 months
            LocalDate current = from.withDayOfMonth(1);
            while (!current.isAfter(to.withDayOfMonth(1))) {
                timeList.add(current.format(monthFormatter));
                current = current.plusMonths(1);
            }
        } else {
            // Up to 2 months
            LocalDate current = from;
            while (!current.isAfter(to)) {
                timeList.add(current.format(dayFormatter));
                current = current.plusDays(1);
            }
        }

        return timeList;
    }

    /**
     * For given searchDate and list of times of either type matches date to correct index in timeList
     * @param searchDate - date to check which section it fits
     * @param timeList - list of used dates in correct format in given report
     *
     * @return List<String>
     * @throws IllegalArgumentException - date not on the list, doesn't fit any section
     * */
    public static int findIndexForDate(Date searchDate, List<String> timeList) {
        LocalDate date = convertToLocalDate(searchDate);
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i).length() == 10 && date.format(dayFormatter).equals(timeList.get(i))) {
                return i;
            } else if (timeList.get(i).length() == 7 && date.format(monthFormatter).equals(timeList.get(i))) {
                return i;
            }
        }

        // If date is not found in the list
        throw new IllegalArgumentException("Date not found in the list");
    }

    /**
     * Converts date from Date type to LocalDate type
     * @param date - date to convert
     *
     * @return LocalDate
     * */
    private static LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
