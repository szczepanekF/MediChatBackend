package pl.logic.site.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.logic.site.model.dao.*;
import pl.logic.site.model.exception.UnknownObjectType;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.ChartSymptom;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Report;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.model.reportsForms.ReportCreateForm;
import pl.logic.site.service.*;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ObjectFacade {
    @Autowired
    private final DoctorService doctorService;
    @Autowired
    private final PatientService patientService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final DiagnosisRequestService diagnosisRequestService;
    @Autowired
    private final ChartService chartService;
    @Autowired
    private final ChartSymptomService chartSymptomService;
    @Autowired
    private final SymptomService symptomService;
    @Autowired
    private final SymptomValuesService symptomValuesService;
    @Autowired
    private final DiseaseSymptomService diseaseSymptomService;
    @Autowired
    private final SpecialisationService specialisationService;
    @Autowired
    private final DictionaryExaminationService dictionaryExaminationService;
    @Autowired
    private final ExaminationService examinationService;
    @Autowired
    private final DiseaseService diseaseService;
    @Autowired
    private final StatisticsService statisticsService;
    @Autowired
    private final ReportService reportService;

    /**
     * Create object of given data access object class using suitable service
     *
     * @param obj - data access object providing data for object creation
     * @return created object
     */
    public Object createObject(Object obj) {
        return switch (obj) {
            case DoctorDAO doctor -> doctorService.createDoctor(doctor);
            case PatientDAO patient -> patientService.createPatient(patient);
            case DiagnosisRequestDAO diagnosisRequest ->
                    diagnosisRequestService.createDiagnosisRequest(diagnosisRequest);
            case ChartDAO chart -> chartService.createChart(chart);
            case ChartSymptomDAO recognition -> chartSymptomService.createChartSymptom(recognition);
            case SpecialisationDAO specialisation -> specialisationService.createSpecialisation(specialisation);
            case ExaminationDAO examination -> examinationService.createExamination(examination);
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }

    /**
     * Get object based on data access object class and ID
     *
     * @param obj - data access object
     * @param id  - id of the object
     * @return retreived object
     */
    public Object getObject(Object obj, int id) {
        return switch (obj) {
            case DoctorDAO doctor -> doctorService.getDoctor(id);
            case PatientDAO patient -> patientService.getPatient(id);
            case DiagnosisRequestDAO diagnosisRequest -> diagnosisRequestService.getDiagnosisRequest(id);
            case ChartDAO chart -> chartService.getChart(id);
            case ChartSymptomDAO recognition -> chartSymptomService.getChartSymptom(id);
            case DiseaseSymptomDAO diseaseSymptomDAO -> diseaseSymptomService.getDiseaseSymptom(id);
            case SymptomDAO symptomDAO -> symptomService.getSymptom(id);
            case SpecialisationDAO specialisation -> specialisationService.getSpecialisation(id);
            case ExaminationDAO examination -> examinationService.getExamination(id);
            case DictionaryExaminationDAO dictionaryExamination ->
                    dictionaryExaminationService.getDictionaryExamination(id);
            case DiseaseDAO disease -> diseaseService.getDisease(id);
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }


    public Object getDiagnosisRequestByChartId(int chartId){
        return diagnosisRequestService.getDiagnosisRequestByChart(chartId);
    }

    public Object getDoctorByDiagnosisRequest(int diagnosisRequestId){
        return doctorService.getDoctorByDiagnosisRequest(diagnosisRequestId);
    }

    public Optional<SpringUser> getUserIdByDoctorOrPatientId(int id, boolean isPatient){
        return userService.findSpringUser(id, isPatient);
    }
    public List<Chart> getChartsByStateAndPatientId(int state, int patientId){
        return chartService.getChartsByStateAndPatientId(state, patientId);
    }

    public List<ChartSymptom> getAllSymptomsByChartId(int chartId) {
        return chartSymptomService.getChartSymptoms(chartId);
    }


    /**
     * Get all objects of class represented by given data access object
     *
     * @param obj - data access object
     * @return all objects of class represented by obj param
     */
    public Object getObjects(Object obj, int filter) {
        return switch (obj) {
            case DoctorDAO doctor -> doctorService.getDoctors(filter);
            case PatientDAO patient -> patientService.getPatients(filter);
            case SpringUser springUser -> userService.getAllUsers(filter);
            case DiagnosisRequestDAO diagnosisRequest -> diagnosisRequestService.getAllDiagnosisRequestsByChart(filter);
            case ChartDAO chart -> chartService.getChartsForPatient(filter);
            case ChartSymptomDAO chartSymptom -> chartSymptomService.getChartSymptoms(filter);
            case DiseaseSymptomDAO diseaseSymptom -> diseaseSymptomService.getDiseaseSymptoms();
            case SymptomDAO symptom -> symptomService.getSymptoms();
            case SymptomValuesDAO symptomValues -> symptomValuesService.getSymptomsValues();
            case SpecialisationDAO specialisation -> specialisationService.getSpecialisations();
            case ExaminationDAO examination -> examinationService.getExaminations(filter);
            case DictionaryExaminationDAO dictionaryExamination ->
                    dictionaryExaminationService.getDictionaryExaminations();
            case DiseaseDAO disease -> diseaseService.getDiseases();
            case ReportDAO reportDAO -> reportService.getReportsByDoctorId(String.valueOf(filter));
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }

    /**
     * Update object based on data access object class and ID
     *
     * @param obj - data access object
     * @param id  - id of the object
     * @return updated object
     */
    public Object updateObject(Object obj, int id) {
        return switch (obj) {
            case DoctorDAO doctor -> doctorService.updateDoctor(doctor, id);
            case PatientDAO patient -> patientService.updatePatient(patient, id);
            case DiagnosisRequestDAO diagnosisRequest ->
                    diagnosisRequestService.updateDiagnosisRequest(diagnosisRequest, id);
            case ChartDAO chart -> chartService.updateChart(chart, id);
            case ChartSymptomDAO chartSymptom -> chartSymptomService.updateChartSymptom(chartSymptom, id);
            case SpecialisationDAO specialisation -> specialisationService.updateSpecialisation(specialisation, id);
            case SpringUserDAO springUser -> userService.updateSpringUser(springUser, id);
            case ExaminationDAO examination -> examinationService.updateExamination(examination, id);
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }

    /**
     * Delete object based on data access object class and ID
     *
     * @param obj - data access object
     * @param id  - id of the object
     */
    public void deleteObject(Object obj, int id) {
        switch (obj) {
            case DoctorDAO doctor -> doctorService.deleteDoctor(id);
            case PatientDAO patient -> patientService.deletePatient(id);
            case DiagnosisRequestDAO diagnosisRequest -> diagnosisRequestService.deleteDiagnosisRequest(id);
            case ChartDAO chart -> chartService.deleteChart(id);
            case ChartSymptomDAO chartSymptom -> chartSymptomService.deleteChartSymptom(id);
            case SpecialisationDAO specialisation -> specialisationService.deleteSpecialisation(id);
            case ExaminationDAO examination -> examinationService.deleteExamination(id);
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        }
    }

    public Report createReport(ReportCreateForm reportCreateForm) {
        return statisticsService.createReport(reportCreateForm);
    }
}