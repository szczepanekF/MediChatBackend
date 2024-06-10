package pl.logic.site.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import io.quickchart.QuickChart;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.enums.ReportFiletype;
import pl.logic.site.model.enums.ReportType;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.*;
import pl.logic.site.model.reportsForms.ReportCreateForm;
import pl.logic.site.model.views.DoctorChat;
import pl.logic.site.model.views.DoctorPatientsWithData;
import pl.logic.site.repository.*;
import pl.logic.site.service.*;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final String[] ageGroups = {"0-5", "6-10", "11-18", "19-30", "31-50", "51-70", "71+"};
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private SymptomRepository symptomRepository;
    @Autowired
    private SpecialisationRepository specialisationRepository;
    @Autowired
    private DiseaseRepository diseaseRepository;
    @Autowired
    private SpringUserRepository springUserRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private ChartService chartService;
    @Autowired
    private DiagnosisRequestService diagnosisRequestService;
    @Autowired
    private ReportRepository reportRepository;


    @Override
    public String[] getAgeGroups() {
        return ageGroups;
    }

    @Override
    @Transactional
    public Report createReport(ReportCreateForm reportCreateForm) throws SQLException {
        Report report = new Report();
        report.setTitle(reportCreateForm.getTitle());
        report.setFiletype(reportCreateForm.getFiletype());
        report.setIdDoctor(String.valueOf(reportCreateForm.getIdDoctor()));

        Blob fileEncoded = extractReportFileEncoded(reportCreateForm);
        if(fileEncoded == null)
            throw new SaveError("Incorrect report type for filetype");
        report.setFile(fileEncoded);

        Report report1 = new Report();
        try {
            report1 = reportRepository.save(report);
        } catch (Exception e){
            e.printStackTrace();
        }

        return report1;
    }

    private Blob extractReportFileEncoded(ReportCreateForm reportCreateForm) throws SQLException {
        if(reportCreateForm.getFiletype() == ReportFiletype.pdf){
            return switch (reportCreateForm.getReportType()){
                case ReportType.user -> createUserReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo(), reportCreateForm.getTitle());
                case ReportType.diseases -> createDiseasesReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo(), reportCreateForm.getTitle());
                case symptoms_date -> null;
                case diseases_date -> null;
                case symptoms_age_groups -> null;
                case diseases_age_groups -> null;
            };
        } else if(reportCreateForm.getFiletype() == ReportFiletype.csv){
            return switch (reportCreateForm.getReportType()){
                case user -> null;
                case diseases -> null;
                case ReportType.symptoms_date -> createSymptomsDateReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases_date -> createDiseasesDateReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.symptoms_age_groups -> createSymptomsAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases_age_groups -> createDiseasesAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
            };
        }
        return null;
    }



    private Blob createUserReport(int idDoctor, Date fromDate, Date toDate, String title) throws SQLException {
        Doctor doctor = doctorRepository.findById(idDoctor).get();
        List<String> messagesPart = getMessagesPart(idDoctor, fromDate, toDate);
        List<String> diagnosisRequestsPart = getDiagnosisRequestsPart(idDoctor, fromDate, toDate);
        List<List<String>> newPatientsPart = getNewPatientsPart(idDoctor, fromDate, toDate);
        List<List<String>> patientAgeGroups = getPatientAgeGroups(idDoctor);
        //create bar chart with 2 series for all patients and my patients: darkblue and blue from front (btn-default but just rgb)
        Object[] symptomAgeGroups = createSymptomAgeGroups(idDoctor, fromDate, toDate);
        //html table to insert
        String symptomAgeGroupsPart = getTableForPDF((List<String>) symptomAgeGroups[0], (List<String>) symptomAgeGroups[1], (List<List<Integer>>) symptomAgeGroups[2], "Age groups", "Symptoms reported by age groups");

        // Generate HTML content
        String htmlContent = generateHtmlContent(doctor, messagesPart, diagnosisRequestsPart, newPatientsPart, patientAgeGroups, symptomAgeGroupsPart, fromDate, toDate, title);

        // Convert HTML to PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, outputStream);

        // Get the PDF content as a byte array
        byte[] pdfBytes = outputStream.toByteArray();

        // Save the PDF to a file or return the file path
        String filePath = null;
        try {
            filePath = savePdfToFile(pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Blob blob = new SerialBlob(pdfBytes);

        return new SerialBlob(pdfBytes);

    }


    private String generateHtmlContent(Doctor doctor, List<String> messagesPart, List<String> diagnosisRequestsPart, List<List<String>> newPatientsPart, List<List<String>> patientAgeGroups, String symptomAgeGroupsPart, Date fromDate, Date toDate, String title) {
        // Fill in the placeholders in the HTML templates with the fetched data
        String doctorName = doctor.getName() + " " + doctor.getSurname();
        String specialisation = specialisationRepository.findById(doctor.getSpecialisation_id()).get().getSpecialisation();
        String fromDateStr = fromDate.toString();
        String toDateStr = toDate.toString();
        String generatedDateStr = new Date().toString();



        // Ensure messagesPart and diagnosisRequestsPart contain the required data
        String receivedMessagesCount = messagesPart.size() > 1 ? messagesPart.get(1) : "0";
        String answeredMessagesCount = messagesPart.size() > 0 ? messagesPart.get(0) : "0";
        String receivedDiagnosisRequestsCount = diagnosisRequestsPart.size() > 1 ? diagnosisRequestsPart.get(1) : "0";
        String answeredDiagnosisRequestsCount = diagnosisRequestsPart.size() > 0 ? diagnosisRequestsPart.get(0) : "0";

        // Read HTML template from file
        String htmlTemplatePath = "src/main/resources/static/pdf/user_report.html";
        String htmlTemplate;
        try {
            htmlTemplate = Files.readString(Paths.get(htmlTemplatePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading HTML template file", e);
        }

        StringBuilder newPatientsTable = new StringBuilder();
        for (List<String> patient : newPatientsPart) {
            String username = patient.get(0);
            String contactedOn = patient.get(1);
            String createdThroughDiagnosisRequest = patient.get(2);

            newPatientsTable.append("<tr style=\"border: 1px solid lightslategray; color: black; height: 40px;\">")
                    .append("<td style=\"padding: 4px;\">").append(username).append("</td>")
                    .append("<td style=\"padding: 4px;\">").append(contactedOn).append("</td>")
                    .append("<td style=\"padding: 4px; text-align:right;\">").append(createdThroughDiagnosisRequest).append("</td>")
                    .append("</tr>");
        }

        // Prepare chart data
        List<Integer> allPatientsData = patientAgeGroups.get(0).stream().map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> myPatientsData = patientAgeGroups.get(1).stream().map(Integer::parseInt).collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        String labelsJson = null;
        String allPatientsDataJson = null;
        String myPatientsDataJson = null;
        try {
            labelsJson = objectMapper.writeValueAsString(ageGroups);
            allPatientsDataJson = objectMapper.writeValueAsString(allPatientsData);
            myPatientsDataJson = objectMapper.writeValueAsString(myPatientsData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting data to JSON", e);
        }

        // Generate chart image using QuickChart
        QuickChart chart = new QuickChart();
        chart.setWidth(800);
        chart.setHeight(600);
        chart.setConfig("{" +
                "type: 'bar'," +
                "data: {" +
                "labels: " + labelsJson + "," +
                "datasets: [{" +
                "label: 'All Patients'," +
                "data: " + allPatientsDataJson + "," +
                "backgroundColor: 'rgba(0, 0, 128, 0.5)'," +
                "borderColor: 'rgba(0, 0, 128, 1)'," +
                "borderWidth: 1" +
                "}, {" +
                "label: 'My Patients'," +
                "data: " + myPatientsDataJson + "," +
                "backgroundColor: 'rgba(0, 0, 255, 0.5)'," +
                "borderColor: 'rgba(0, 0, 255, 1)'," +
                "borderWidth: 1" +
                "}]" +
                "}," +
                "options: {" +
                "scales: {" +
                "x: {" +
                "beginAtZero: true" +
                "}," +
                "y: {" +
                "beginAtZero: true" +
                "}" +
                "}" +
                "}" +
                "}");

        String chartUrl = chart.getUrl();


        String filledHtmlContent = htmlTemplate
                .replace("{Name Surname}", doctorName)
                .replace("{Specialisation}", specialisation)
                .replace("{Title}", title)
                .replace("{from}", fromDateStr)
                .replace("{to}", toDateStr)
                .replace("{yyyy-mm-dd H:i}", generatedDateStr)
                .replace("{received_messages_count}", receivedMessagesCount)
                .replace("{answered_messages_count}", answeredMessagesCount)
                .replace("{received_diagnosis_requests_count}", receivedDiagnosisRequestsCount)
                .replace("{answered_diagnosis_requests_count}", answeredDiagnosisRequestsCount)
                .replace("<!--NEW_PATIENTS_TABLE-->", newPatientsTable.toString())
                .replace("<!--NEW_PATIENTS_TABLE2-->", symptomAgeGroupsPart)
                .replace("{new_patient_count}", String.valueOf(newPatientsPart.size()))
                .replace("{chartUrl}", chartUrl);

        return filledHtmlContent;
    }

    private String generateHtmlContent2(Doctor doctor, Date fromDate, Date toDate, String title, String symptom_date_table_part, String diseases_date_table_part, String symptom_age_groups_table_part, String diseases_age_groups_table_part) {
        // Fill in the placeholders in the HTML templates with the fetched data
        String doctorName = doctor.getName() + " " + doctor.getSurname();
        String specialisation = specialisationRepository.findById(doctor.getSpecialisation_id()).get().getSpecialisation();
        String fromDateStr = fromDate.toString();
        String toDateStr = toDate.toString();
        String generatedDateStr = new Date().toString();

        // Read HTML template from file
        String htmlTemplatePath = "src/main/resources/static/pdf/diseases_report.html";
        String htmlTemplate;
        try {
            htmlTemplate = Files.readString(Paths.get(htmlTemplatePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading HTML template file", e);
        }

        String filledHtmlContent = htmlTemplate
                .replace("{Name Surname}", doctorName)
                .replace("{Specialisation}", specialisation)
                .replace("{Title}", title)
                .replace("{from}", fromDateStr)
                .replace("{to}", toDateStr)
                .replace("{yyyy-mm-dd H:i}", generatedDateStr)
                .replace("{symptom_date_table_part}", symptom_date_table_part)
                .replace("{diseases_date_table_part}", diseases_date_table_part)
                .replace("{symptom_age_groups_table_part}", symptom_age_groups_table_part)
                .replace("{diseases_age_groups_table_part}", diseases_age_groups_table_part);

        return filledHtmlContent;
    }


    private String savePdfToFile(byte[] pdfBytes) throws IOException {
        // Save the PDF to a file and return the file path
        String filePath = "report.pdf";
        java.nio.file.Files.write(java.nio.file.Paths.get(filePath), pdfBytes);
        return filePath;
    }

    private List<String> getMessagesPart(int idDoctor, Date fromDate, Date toDate){
        List<String> messagesPart = new ArrayList<>();
        int answered = 0;
        int received = 0;
        //get doctor chats
        List<DoctorChat> chats = doctorService.getMyChats(idDoctor);
        //foreach chat get messages from timespan
        for (DoctorChat chat : chats) {
            //if message recipient is doctor add to received else to answered
            for (Message message : chat.getMessagesByDates(fromDate, toDate)) {
                if(message.getRecipientId() == springUserRepository.getByDoctorId(idDoctor).getId()){
                    received += 1;
                } else  {
                    answered += 1;
                }
            }
        }
        messagesPart.add(String.valueOf(answered));
        messagesPart.add(String.valueOf(received));
        return messagesPart;
    }

    private List<String> getDiagnosisRequestsPart(int idDoctor, Date fromDate, Date toDate){
        List<String> diagnosisRequestsPart = new ArrayList<>();
        int diagnosed = 0;
        int received = 0;
        //get doctor diagnosis requests for given time
        List<DiagnosisRequest> diagnosisRequests = doctorService.getMyDiagnosisRequests(idDoctor, fromDate, toDate);
        //count all as received
        //count all having diagnosis as diagnosed
        for(DiagnosisRequest diagnosisRequest : diagnosisRequests){
            if(diagnosisRequest.getDiagnosis().isEmpty()){
                received+=1;
            } else {
                diagnosed+=1;
            }
        }
        diagnosisRequestsPart.add(String.valueOf(diagnosed));
        diagnosisRequestsPart.add(String.valueOf(received));
        return diagnosisRequestsPart;
    }

    private List<List<String>> getNewPatientsPart(int idDoctor, Date fromDate, Date toDate){
        List<List<String>> newPatients = new ArrayList<>();
        //fetch new patients for doctor in timeframe
        List<DoctorPatientsWithData> patients = doctorService.getMyPatientsByDate(idDoctor, fromDate, toDate);
        //foreach patient chosen create row with name surname, chat creation date, if started with diagnosis request
        for (DoctorPatientsWithData patient : patients){
            List<String> patientData = new ArrayList<>();
            patientData.add(patient.getPatient().getName()+" "+patient.getPatient().getSurname());
            patientData.add(patient.getDateofcontact().toString());
            patientData.add(patient.getIsDiagnosisRequest());
            newPatients.add(patientData);
        }
        return newPatients;
    }

    private List<List<String>> getPatientAgeGroups(int idDoctor){
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
        for (Patient patient : allPatients){
            int ageGroup = findAgeGroupIndex(patientService.getAge(patient.getId()));
            all_patients_age_groups.set(ageGroup, all_patients_age_groups.get(ageGroup) + 1);
            if(doctorService.getMyPatients(idDoctor).contains(patient)){
                my_patients_age_groups.set(ageGroup, my_patients_age_groups.get(ageGroup) + 1);
            }
        }

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
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Blob createDiseasesReport(int idDoctor, Date fromDate, Date toDate, String title) throws SQLException {
        Doctor doctor = doctorRepository.findAllById(idDoctor);

        // Generate HTML content for each table
        Object[] symptomDate = createSymptomsDate(idDoctor, fromDate, toDate);
        String symptom_date_table_part = getTableForPDF((List<String>) symptomDate[0], (List<String>) symptomDate[1], (List<List<Integer>>) symptomDate[2], (String) symptomDate[3], "Symptoms reported by date");

        Object[] diseasesDate = createDiseasesDate(idDoctor, fromDate, toDate);
        String diseases_date_table_part = getTableForPDF((List<String>) diseasesDate[0], (List<String>) diseasesDate[1], (List<List<Integer>>) diseasesDate[2], (String) diseasesDate[3], "Diseases diagnosed by date");

        Object[] symptomAgeGroups = createSymptomAgeGroups(idDoctor, fromDate, toDate);
        String symptom_age_groups_table_part = getTableForPDF((List<String>) symptomAgeGroups[0], (List<String>) symptomAgeGroups[1], (List<List<Integer>>) symptomAgeGroups[2], "Age groups", "Symptoms reported by age groups");

        Object[] diseasesAgeGroups = createDiseasesAgeGroups(idDoctor, fromDate, toDate);
        String diseases_age_groups_table_part = getTableForPDF((List<String>) diseasesAgeGroups[0], (List<String>) diseasesAgeGroups[1], (List<List<Integer>>) diseasesAgeGroups[2], "Age groups", "Diseases diagnosed to age groups");

        // Generate HTML content
        String filledHtmlContent = generateHtmlContent2(doctor, fromDate, toDate, title, symptom_date_table_part, diseases_date_table_part, symptom_age_groups_table_part, diseases_age_groups_table_part);

        // Convert HTML to PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(filledHtmlContent, outputStream);

        // Get the PDF content as a byte array
        byte[] pdfBytes = outputStream.toByteArray();

        // Save the PDF to a file or return the file path
        String filePath = null;
        try {
            filePath = savePdfToFile(pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Blob blob = new SerialBlob(pdfBytes);

        return blob;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Blob createSymptomsDateReport(int idDoctor, Date fromDate, Date toDate) throws SQLException {
        Object[] symptomDate = createSymptomsDate(idDoctor, fromDate, toDate);
        List<List<Object>> csvTable = getTableForCSV((List<String>) symptomDate[0], (List<String>) symptomDate[1], (List<List<Integer>>) symptomDate[2], (String) symptomDate[3]);
        // Generate CSV file
        String csvContent = generateCSV(csvTable);

        // Save to file
        Path csvFilePath = saveCSVToFile(csvContent, "symptoms_date_report.csv");

        // Encode CSV content to Base64

        Blob blob = new SerialBlob(csvContent.getBytes(StandardCharsets.UTF_8));

        return blob;
//        return Base64.getEncoder().encodeToString(csvContent.getBytes(StandardCharsets.UTF_8));
    }

    private Object[] createSymptomsDate(int idDoctor, Date fromDate, Date toDate){
        List<Symptom> symptoms = symptomRepository.findAll();
        List<String> symptomNames = new ArrayList<>();
        for(int i = 0; i < symptoms.size(); i++){
            symptomNames.add(symptoms.get(i).getName());
        }
        List<String> dateRanges = generateDateRange(fromDate, toDate);
        List<List<Integer>> data = new ArrayList<>();
        //init data table
        for(int i = 0; i < dateRanges.size(); i++){
            data.add(new ArrayList<>());
            for(int j = 0; j < symptoms.size(); j++){
                data.get(i).add(0);
            }
        }

        List<Chart> charts = doctorService.getMyCharts(idDoctor, fromDate, toDate);
        for(Chart chart : charts){
            int dateIndex = findIndexForDate(chart.getDate(), dateRanges);

            List<Symptom> chartSymptoms = chartService.getSymptoms(chart.getId());
            for(Symptom symptom : chartSymptoms){
                int symptomIndex = findSymptomIndex(symptoms, symptom);
                data.get(dateIndex).set(symptomIndex, data.get(dateIndex).get(symptomIndex) + 1);
            }
        }

        Object[] symptomsDate = new Object[4];
        symptomsDate[0] = symptomNames;
        symptomsDate[1] = dateRanges;
        symptomsDate[2] = data;
        symptomsDate[3] = dateRanges.getFirst().length() == 10 ? "Dates" : "Months";
        return symptomsDate;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Blob createDiseasesDateReport(int idDoctor, Date fromDate, Date toDate) throws SQLException {
        Object[] diseasesDate = createDiseasesDate(idDoctor, fromDate, toDate);
        List<List<Object>> csvTable = getTableForCSV((List<String>) diseasesDate[0], (List<String>) diseasesDate[1], (List<List<Integer>>) diseasesDate[2], (String) diseasesDate[3]);

        String csvContent = generateCSV(csvTable);

        // Save to file
        Path csvFilePath = saveCSVToFile(csvContent, "diseases_date_report.csv");

        Blob blob = new SerialBlob(csvContent.getBytes(StandardCharsets.UTF_8));

        return blob;

    }

    private Object[] createDiseasesDate(int idDoctor, Date fromDate, Date toDate){
        List<Disease> diseases = diseaseRepository.findAll();
        List<String> diseasesNames = new ArrayList<>();
        for (Disease disease : diseases) {
            diseasesNames.add(disease.getName());
        }
        List<String> dateRanges = generateDateRange(fromDate, toDate);
        List<List<Integer>> data = new ArrayList<>();
        //init data table
        for(int i = 0; i < dateRanges.size(); i++){
            data.add(new ArrayList<>());
            for(int j = 0; j < diseases.size(); j++){
                data.get(i).add(0);
            }
        }


        List<DiagnosisRequest> diagnosisRequests = doctorService.getMyDiagnosisRequests(idDoctor, fromDate, toDate);
        for(DiagnosisRequest diagnosisRequest : diagnosisRequests){
            if(diagnosisRequest.getIdDisease() == -1 ||diagnosisRequest.getIdDisease()  == 0)
                continue;

            int dateIndex = findIndexForDate(diagnosisRequest.getModificationDate(), dateRanges);

            int diseaseIndex = findDiseaseIndexById(diseases, diagnosisRequest.getIdDisease());
            data.get(dateIndex).set(diseaseIndex, data.get(dateIndex).get(diseaseIndex) + 1);
        }


        Object[] diseasesDate = new Object[4];
        diseasesDate[0] = diseasesNames;
        diseasesDate[1] = dateRanges;
        diseasesDate[2] = data;
        diseasesDate[3] = dateRanges.getFirst().length() == 10 ? "Dates" : "Months";
        return diseasesDate;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Blob createSymptomsAgeGroupsReport(int idDoctor, Date fromDate, Date toDate) throws SQLException {
        Object[] symptomAgeGroups = createSymptomAgeGroups(idDoctor, fromDate, toDate);
        List<List<Object>> csvTable = getTableForCSV((List<String>) symptomAgeGroups[0], (List<String>) symptomAgeGroups[1], (List<List<Integer>>) symptomAgeGroups[2], "Age groups");

        String csvContent = generateCSV(csvTable);

        // Save to file
        Path csvFilePath = saveCSVToFile(csvContent, "symptoms_age_report.csv");

        Blob blob = new SerialBlob(csvContent.getBytes(StandardCharsets.UTF_8));

        return blob;
    }

    private Object[] createSymptomAgeGroups(int idDoctor, Date fromDate, Date toDate){
        List<Symptom> symptoms = symptomRepository.findAll();
        List<String> symptomNames = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            symptomNames.add(symptom.getName());
        }
        List<List<Integer>> data = new ArrayList<>();
        //init data table
        for(int i = 0; i < ageGroups.length; i++){
            data.add(new ArrayList<>());
            for(int j = 0; j < symptoms.size(); j++){
                data.get(i).add(0);
            }
        }


        List<Chart> charts = doctorService.getMyCharts(idDoctor, fromDate, toDate);
        for(Chart chart : charts){
            Patient chartPatient = chartService.getPatient(chart.getId());
            int ageIndex = findAgeGroupIndex(patientService.getAge(chartPatient.getId()));

            List<Symptom> chartSymptoms = chartService.getSymptoms(chart.getId());
            for(Symptom symptom : chartSymptoms){
                int symptomIndex = findSymptomIndex(symptoms, symptom);
                data.get(ageIndex).set(symptomIndex, data.get(ageIndex).get(symptomIndex) + 1);
            }
        }

        Object[] ageGroupsData = new Object[3];
        ageGroupsData[0] = symptomNames;
        ageGroupsData[1] = Arrays.asList(ageGroups);
        ageGroupsData[2] = data;
        return ageGroupsData;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Blob createDiseasesAgeGroupsReport(int idDoctor, Date fromDate, Date toDate) throws SQLException {
        Object[] diseasesAgeGroups = createDiseasesAgeGroups(idDoctor, fromDate, toDate);
        List<List<Object>> csvTable = getTableForCSV((List<String>) diseasesAgeGroups[0], (List<String>) diseasesAgeGroups[1], (List<List<Integer>>) diseasesAgeGroups[2], "Age groups");

        String csvContent = generateCSV(csvTable);

        // Save to file
        Path csvFilePath = saveCSVToFile(csvContent, "diseases_age_report.csv");

        Blob blob = new SerialBlob(csvContent.getBytes(StandardCharsets.UTF_8));

        return blob;
    }

    private Object[] createDiseasesAgeGroups(int idDoctor, Date fromDate, Date toDate){
        List<Disease> diseases = diseaseRepository.findAll();
        List<String> diseasesNames = new ArrayList<>();
        for (Disease disease : diseases) {
            diseasesNames.add(disease.getName());
        }
        List<List<Integer>> data = new ArrayList<>();
        //init data table
        for(int i = 0; i < ageGroups.length; i++){
            data.add(new ArrayList<>());
            for(int j = 0; j < diseases.size(); j++){
                data.get(i).add(0);
            }
        }


        List<DiagnosisRequest> diagnosisRequests = doctorService.getMyDiagnosisRequests(idDoctor, fromDate, toDate);
        for(DiagnosisRequest diagnosisRequest : diagnosisRequests){

            Patient diagnosisRequestPatient = diagnosisRequestService.getPatient(diagnosisRequest.getId());
            int ageIndex = findAgeGroupIndex(patientService.getAge(diagnosisRequestPatient.getId()));

            int diseaseIndex = findDiseaseIndexById(diseases, diagnosisRequest.getIdDisease());
            if(diseaseIndex == -1)
                continue;

            data.get(ageIndex).set(diseaseIndex, data.get(ageIndex).get(diseaseIndex) + 1);
        }

        Object[] ageGroupsData = new Object[3];
        ageGroupsData[0] = diseasesNames;
        ageGroupsData[1] = Arrays.asList(ageGroups);
        ageGroupsData[2] = data;
        return ageGroupsData;
    }

    @Override
    public int findSymptomIndex(List<Symptom> symptomsList, Symptom symptom) {
        for (int i = 0; i < symptomsList.size(); i++) {
            if (symptomsList.get(i).getId() == symptom.getId()) {
                return i;
            }
        }
        return -1; // Symptom not found in the list
    }

    @Override
    public int findDiseaseIndex(List<Disease> diseasesList, Disease disease) {
        for (int i = 0; i < diseasesList.size(); i++) {
            if (diseasesList.get(i).getId() == disease.getId()) {
                return i;
            }
        }
        return -1; // Disease not found in the list
    }

    private int findDiseaseIndexById(List<Disease> diseasesList, int idDisease) {
        for (int i = 0; i < diseasesList.size(); i++) {
            if (diseasesList.get(i).getId() == idDisease) {
                return i;
            }
        }
        return -1; // Disease not found in the list
    }

    @Override
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
    @Override
    public List<String> generateDateRange(Date fromDate, Date toDate) {
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
    @Override
    public int findIndexForDate(Date searchDate, List<String> timeList) {
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
    @Override
    public LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String getTableForPDF(List<String> columns, List<String> rows, List<List<Integer>> data, String rowsName, String tableTitle) {
        // Prepare the data for the HTML table
        List<List<String>> result = new ArrayList<>();

        // Add header row
        result.add(new ArrayList<>());
        result.get(0).add(""); // Placeholder for the empty cell in the top-left corner
        for (String column : columns) {
            result.get(0).add(column);
        }

        // Add data rows
        for (int i = 0; i < rows.size(); i++) {
            result.add(new ArrayList<>());
            result.get(i + 1).add(rows.get(i)); // First cell in each row is the row name
            for (Integer datum : data.get(i)) {
                result.get(i + 1).add(datum.toString());
            }
        }

        // Read HTML template from file
        String htmlTemplatePath = "src/main/resources/static/pdf/_data_table.html";
        String htmlTemplate;
        try {
            htmlTemplate = Files.readString(Paths.get(htmlTemplatePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading HTML template file", e);
        }

        // Replace placeholders in the HTML template with actual data
        String tableHtml = htmlTemplate.replace("{table_title}", tableTitle);

        // Generate table rows dynamically
        StringBuilder tableRows = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            List<String> row = result.get(i);
            if (i == 0) {
                // Header row
                tableRows.append("<tr style=\"border: 1px solid lightslategray; background-color: lightslategray; color: white; font-weight: 600;\">");
            } else {
                tableRows.append("<tr style=\"border: 1px solid lightslategray; color: black; height: 40px;\">");
            }
            for (int j = 0; j < row.size(); j++) {
                if (i == 0 && j == 0) {
                    // Empty cell in the top-left corner
                    tableRows.append("<th style=\"height: 40px; padding: 4px;\"></th>");
                } else if (i == 0) {
                    // Header cells
                    tableRows.append("<th style=\"height: 40px; padding: 4px;\">").append(row.get(j)).append("</th>");
                } else {
                    // Data cells
                    tableRows.append("<td style=\"padding: 4px;\">").append(row.get(j)).append("</td>");
                }
            }
            tableRows.append("</tr>");
        }

        // Insert the generated table rows into the HTML template
        tableHtml = tableHtml.replace("<!-- Dynamic Table Rows -->", tableRows.toString());

        return tableHtml;
    }

    private List<List<Object>> getTableForCSV(List<String> columns, List<String> rows, List<List<Integer>> data, String rowsName){
        List<List<Object>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        result.getFirst().add(rowsName);
        for (String column : columns) {
            result.getFirst().add(column);
        }
        for(int i=1; i <= rows.size(); i++){
            result.add(new ArrayList<>());
            result.get(i).add(rows.get(i-1));
            for(int j=1; j <= columns.size(); j++){
                result.get(i).add(data.get(i-1).get(j-1));
            }
        }
        return result;
    }

    private Path saveCSVToFile(String csvContent, String fileName) {
        try {
            Path path = Paths.get(fileName);
            Files.write(path, csvContent.getBytes(StandardCharsets.UTF_8));
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Error while saving CSV to file", e);
        }
    }

    private String generateCSV(List<List<Object>> csvTable) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (List<Object> row : csvTable) {
                csvPrinter.printRecord(row);
            }

            csvPrinter.flush();
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while generating CSV", e);
        }
    }


    public List<List<Object>> getSymptomsDate(int idDoctor, Date fromDate, Date toDate){
        Object[] symptomDate = createSymptomsDate(idDoctor, fromDate, toDate);
        return getTableForCSV((List<String>) symptomDate[0], (List<String>) symptomDate[1], (List<List<Integer>>) symptomDate[2], (String) symptomDate[3]);
    }
    public List<List<Object>> getDiseasesDate(int idDoctor, Date fromDate, Date toDate){
        Object[] symptomDate = createDiseasesDate(idDoctor, fromDate, toDate);
        return getTableForCSV((List<String>) symptomDate[0], (List<String>) symptomDate[1], (List<List<Integer>>) symptomDate[2], (String) symptomDate[3]);
    }
    public List<List<Object>> getSymptomsAgeGroups(int idDoctor, Date fromDate, Date toDate){
        Object[] symptomDate = createSymptomAgeGroups(idDoctor, fromDate, toDate);
        return getTableForCSV((List<String>) symptomDate[0], (List<String>) symptomDate[1], (List<List<Integer>>) symptomDate[2], "Age groups");
    }
    public List<List<Object>> getDiseasesAgeGroups(int idDoctor, Date fromDate, Date toDate){
        Object[] symptomDate = createDiseasesAgeGroups(idDoctor, fromDate, toDate);
        return getTableForCSV((List<String>) symptomDate[0], (List<String>) symptomDate[1], (List<List<Integer>>) symptomDate[2], "Age groups");
    }
}