package pl.logic.site.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.exception.UnknownObjectType;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.service.DiagnosisRequestService;
import pl.logic.site.service.DoctorService;
import pl.logic.site.service.PatientService;
import pl.logic.site.service.UserService;
import pl.logic.site.utils.Consts;

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


    /**
     * Create user of given data access object class using suitable service
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
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }

    /**
     * Get user based on data access object class and ID
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
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }

    /**
     * Get all users of class represented by given data access object
     *
     * @param obj - data access object
     * @return all users of class represented by user param
     */
    public Object getObjects(Object obj, int filter) {
        return switch (obj) {
            case DoctorDAO doctor -> doctorService.getDoctors(filter);
            case PatientDAO patient -> patientService.getPatients();
            case SpringUser springUser -> userService.getAllUsers(filter);
            case DiagnosisRequestDAO diagnosisRequest -> diagnosisRequestService.getDiagnosisRequests();
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }

    /**
     * Update user based on data access object class and user ID
     *
     * @param obj - data access object
     * @param id  - id of the user
     * @return updated user
     */
    public Object updateObject(Object obj, int id) {
        return switch (obj) {
            case DoctorDAO doctor -> doctorService.updateDoctor(doctor, id);
            case PatientDAO patient -> patientService.updatePatient(patient, id);
            case DiagnosisRequestDAO diagnosisRequest ->
                    diagnosisRequestService.updateDiagnosisRequest(diagnosisRequest, id);
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        };
    }

    /**
     * Delete user based on data access object class and user ID
     *
     * @param obj - data access object
     * @param id  - id of the user
     */
    public void deleteObject(Object obj, int id) {
        switch (obj) {
            case DoctorDAO doctor -> doctorService.deleteDoctor(id);
            case PatientDAO patient -> patientService.deletePatient(id);
            case DiagnosisRequestDAO diagnosisRequest -> diagnosisRequestService.deleteDiagnosisRequest(id);
            default -> throw new UnknownObjectType(Consts.C452_UKNOWN_OBJECT_TYPE);
        }
    }
}