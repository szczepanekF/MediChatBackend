package pl.logic.site.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.repository.DiagnosisRequestRepository;
import pl.logic.site.service.ChartService;
import pl.logic.site.service.DiagnosisRequestService;
import pl.logic.site.utils.Consts;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
@Service
public class DiagnosisRequestServiceImpl implements DiagnosisRequestService {


    private final DiagnosisRequestRepository diagnosisRequestRepository;
    private final MessageServiceImpl messageService;
    private final ChartService chartService;
    private final UserServiceImpl userService;

    /**
     * Create diagnosis request based on given data access object
     *
     * @param diagnosisRequest - data access object
     * @return created diagnosis request
     */
    @Override
    @Transactional
    public DiagnosisRequest createDiagnosisRequest(DiagnosisRequestDAO diagnosisRequest) {
        DiagnosisRequest diagnosisRequestEntity = new DiagnosisRequest(
                diagnosisRequest.diagnosisRequest().getId(),
                diagnosisRequest.diagnosisRequest().getIdChart(),
                diagnosisRequest.diagnosisRequest().getIdDoctor(),
                diagnosisRequest.diagnosisRequest().getDiagnosis(),
                diagnosisRequest.diagnosisRequest().getVoiceDiagnosis(),
                diagnosisRequest.diagnosisRequest().getCreationDate(),
                diagnosisRequest.diagnosisRequest().getModificationDate()
                );
        if (diagnosisRequestEntity.getId() != 0) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + diagnosisRequestEntity);
            log.error(err.getMessage());
            throw err;
        }
        DiagnosisRequest returned;
        try {
            returned = diagnosisRequestRepository.saveAndFlush(diagnosisRequestEntity);
            sendRequestDiagnosisMessage(returned);
        } catch (Exception e) {
            log.error(e.getMessage());
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " " + diagnosisRequestEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Diagnosis request was successfully created: {}", returned);
        return returned;
    }


    /**
     * Create and send a diagnostic request message
     *
     * @param diagnosisRequest - diagnosis request on which sent message will be based on
     */
    private void sendRequestDiagnosisMessage(DiagnosisRequest diagnosisRequest) {
        int patientId = chartService.getChart(diagnosisRequest.getIdChart()).getIdPatient();
        log.error(String.valueOf(patientId));
        int doctorId = diagnosisRequest.getIdDoctor();
        log.error(String.valueOf(doctorId));

        patientId = userService.findSpringUser(patientId,true).orElseThrow().getId();
        doctorId = userService.findSpringUser(doctorId,false).orElseThrow().getId();
        Message message = new Message(0, "", patientId, doctorId, "Request diagnosis", new Date());
        messageService.save(message);
    }

    /**
     * Delete diagnosis request with given id
     *
     * @param id - id of the diagnosis request
     */
    @Override
    public void deleteDiagnosisRequest(int id) {
        Optional<DiagnosisRequest> diagnosisRequest = diagnosisRequestRepository.findById(id);
        if (diagnosisRequest.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            throw err;
        }
        try {
            diagnosisRequestRepository.deleteById(diagnosisRequest.get().getId());
        } catch (Exception e) {
            DeleteError err = new DeleteError(Consts.C455_DELETING_ERROR + " " + diagnosisRequest);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Diagnosis request with id: {} was successfully deleted", id);
    }

    /**
     * Update diagnosis request based on diagnosis request data access object and diagnosis requests id
     *
     * @param diagnosisRequest - data access object
     * @param id               - id of the diagnosis request
     * @return updated diagnosis request
     */
    @Override
    public DiagnosisRequest updateDiagnosisRequest(DiagnosisRequestDAO diagnosisRequest, int id) {
        DiagnosisRequest diagnosisRequestEntity = new DiagnosisRequest(
                diagnosisRequest.diagnosisRequest().getId(),
                diagnosisRequest.diagnosisRequest().getIdChart(),
                diagnosisRequest.diagnosisRequest().getIdDoctor(),
                diagnosisRequest.diagnosisRequest().getDiagnosis(),
                diagnosisRequest.diagnosisRequest().getVoiceDiagnosis(),
                diagnosisRequest.diagnosisRequest().getCreationDate(),
                diagnosisRequest.diagnosisRequest().getModificationDate()
                );
        Optional<DiagnosisRequest> diagnosisRequestFromDatabase = diagnosisRequestRepository.findById(id);
        if (diagnosisRequestFromDatabase.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + diagnosisRequestEntity);
            log.error(err.getMessage());
            throw err;
        }
        DiagnosisRequest returned;
        try {
            returned = diagnosisRequestRepository.saveAndFlush(diagnosisRequestEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + diagnosisRequestEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Diagnosis request with id: {} was successfully updated: {}", id, returned);
        return returned;
    }

    /**
     * Get diagnosis request entity by id
     *
     * @param id - id of the diagnosis request
     * @return diagnosis request with given id
     */
    @Override
    public DiagnosisRequest getDiagnosisRequest(int id) {
        return diagnosisRequestRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get diagnosis request entity by chart id
     *
     * @param id - id of the chart
     * @return diagnosis request assigned to given
     */
    @Override
    public DiagnosisRequest getDiagnosisRequestByChart(int id) {
        return diagnosisRequestRepository.findByIdChart(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " Chart ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all diagnosis requests
     *
     * @return list of all diagnosis requests
     */
    @Override
    public List<DiagnosisRequest> getDiagnosisRequests(int filter) {
        List<DiagnosisRequest> diagnosisRequests = diagnosisRequestRepository.findAllByIdChart(filter);
        if (diagnosisRequests.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All diagnosis requests were successfully retrieved");
        return diagnosisRequests;
    }
}
