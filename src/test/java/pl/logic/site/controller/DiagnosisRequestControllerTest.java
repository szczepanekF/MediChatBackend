package pl.logic.site.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.*;

import pl.logic.site.service.LoggingService;
import pl.logic.site.service.impl.EmailServiceImpl;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DiagnosisRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;

    @MockBean
    private EmailServiceImpl emailService;

    private DiagnosisRequest diagnosisRequest;

    @BeforeEach
    public void setup() {
        diagnosisRequest = DiagnosisRequest.builder().id(1).idChart(2).idDoctor(3).creationDate(new Date()).modificationDate(new Date()).build();
    }

    @Test
    public void testCreateDiagnosisRequestSuccessWithSentMail() throws Exception {
        when(objectFacade.createObject(any(DiagnosisRequestDAO.class))).thenReturn(diagnosisRequest);
        Doctor doc = Doctor.builder().id(1).isBot(0).build();
        Patient patient = Patient.builder().id(2).name("TEST").surname("surnameTEST").build();
        SpringUser springUser = SpringUser.builder().id(1).email("test@example.com").build();
        when(objectFacade.getDoctorByDiagnosisRequest(diagnosisRequest.getId())).thenReturn(doc);
        when(objectFacade.getObject(any(ChartDAO.class), eq(diagnosisRequest.getIdChart()))).thenReturn(Chart.builder().idPatient(2).build());
        when(objectFacade.getObject(any(PatientDAO.class), eq(2))).thenReturn(patient);
        when(objectFacade.getUserIdByDoctorOrPatientId(doc.getId(), false)).thenReturn(Optional.of(springUser));
        mockMvc.perform(post("/diagnosisRequestController/diagnosisRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "diagnosisRequest": {
                                    "id": 0,
                                    "idChart": 2,
                                    "idDoctor": 3,
                                    "diagnosis": "",
                                    "idDisease": 0,
                                    "voiceDiagnosis": "",
                                     "creationDate": "",
                                     "modificationDate": ""
                                  }
                                }""")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idChart").value(diagnosisRequest.getIdChart()))
                .andExpect(jsonPath("$.containedObject.idDoctor").value(diagnosisRequest.getIdDoctor()))
                .andExpect(jsonPath("$.containedObject.diagnosis").value(diagnosisRequest.getDiagnosis()))
                .andExpect(jsonPath("$.containedObject.idDisease").value(diagnosisRequest.getIdDisease()))
                .andExpect(jsonPath("$.containedObject.voiceDiagnosis").value(diagnosisRequest.getVoiceDiagnosis()))
                .andExpect(jsonPath("$.containedObject.creationDate").value(diagnosisRequest.getCreationDate()))
                .andExpect(jsonPath("$.containedObject.modificationDate").value(diagnosisRequest.getModificationDate()));

        Map<String, String> emailParameters = new HashMap<>() {{
            put("requestUserFullName", patient.getName() + " " + patient.getSurname());
            put("emailAddress", springUser.getEmail());
            put("name", doc.getName());
            put("date", "");
            put("requestContent", "");
            put("thisUserId", String.valueOf(springUser.getId()));
            put("patientUserId", String.valueOf(patient.getId()));
            put("subject", "REQUEST DIAGNOSIS");
        }};
        verify(objectFacade, times(1)).createObject(any(DiagnosisRequestDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
        verify(emailService, never()).sendEmail(any(), eq(emailParameters));
    }

    @Test
    public void testCreateDiagnosisRequestSuccessWithoutSentMail() throws Exception {
        when(objectFacade.createObject(any(DiagnosisRequestDAO.class))).thenReturn(diagnosisRequest);
        Doctor doc = Doctor.builder().id(1).isBot(0).build();
        Patient patient = Patient.builder().id(2).name("TEST").surname("surnameTEST").build();
        SpringUser springUser = SpringUser.builder().id(1).email("test@example.com").build();
        when(objectFacade.getDoctorByDiagnosisRequest(diagnosisRequest.getId())).thenReturn(doc);
        when(objectFacade.getObject(any(ChartDAO.class), eq(diagnosisRequest.getIdChart()))).thenReturn(Chart.builder().idPatient(2).build());
        when(objectFacade.getObject(any(PatientDAO.class), eq(2))).thenReturn(patient);
        when(objectFacade.getUserIdByDoctorOrPatientId(doc.getId(), false)).thenReturn(Optional.of(springUser));
        mockMvc.perform(post("/diagnosisRequestController/diagnosisRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "diagnosisRequest": {
                                    "id": 1,
                                    "idChart": 2,
                                    "idDoctor": 3,
                                    "diagnosis": "",
                                    "idDisease": 0,
                                    "voiceDiagnosis": "",
                                    "creationDate": "",
                                    "modificationDate": ""
                                  }
                                }""")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.containedObject.id").value("1"));

        verify(objectFacade, times(1)).createObject(any(DiagnosisRequestDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
        verify(emailService, times(1)).sendEmail(any(), any());
    }

    @Test
    public void testCreateDiagnosisRequestSaveError() throws Exception {
        doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(DiagnosisRequestDAO.class));

        mockMvc.perform(post("/diagnosisRequestController/diagnosisRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "diagnosisRequest": {
                                    "id": 0,
                                    "idChart": 2,
                                    "idDoctor": 3,
                                    "diagnosis": "",
                                    "idDisease": 0,
                                    "voiceDiagnosis": "",
                                    "creationDate": "",
                                    "modificationDate": ""
                                  }
                                }"""))
                .andExpect(status().is(453));
        verify(objectFacade, times(1)).createObject(any(DiagnosisRequestDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());


    }

    @Test
    public void testGetDiagnosisRequestsByChartIdSuccess() throws Exception {
        when(objectFacade.getDiagnosisRequestByChartId(2)).thenReturn(diagnosisRequest);

        mockMvc.perform(get("/diagnosisRequestController/diagnosisRequestByChart/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"));
        verify(objectFacade, times(1)).getDiagnosisRequestByChartId(2);
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetDiagnosisRequestsByChartIdNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getDiagnosisRequestByChartId(2);

        mockMvc.perform(get("/diagnosisRequestController/diagnosisRequestByChart/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getDiagnosisRequestByChartId(2);
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetDiagnosisRequestSuccess() throws Exception {
        when(objectFacade.getObject(any(DiagnosisRequestDAO.class), eq(1))).thenReturn(diagnosisRequest);

        mockMvc.perform(get("/diagnosisRequestController/diagnosisRequest/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idChart").value(diagnosisRequest.getIdChart()))
                .andExpect(jsonPath("$.containedObject.idDoctor").value(diagnosisRequest.getIdDoctor()))
                .andExpect(jsonPath("$.containedObject.diagnosis").value(diagnosisRequest.getDiagnosis()))
                .andExpect(jsonPath("$.containedObject.idDisease").value(diagnosisRequest.getIdDisease()))
                .andExpect(jsonPath("$.containedObject.voiceDiagnosis").value(diagnosisRequest.getVoiceDiagnosis()))
                .andExpect(jsonPath("$.containedObject.creationDate").value(diagnosisRequest.getCreationDate()))
                .andExpect(jsonPath("$.containedObject.modificationDate").value(diagnosisRequest.getModificationDate()));

        verify(objectFacade, times(1)).getObject(any(DiagnosisRequestDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetDiagnosisRequestNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(DiagnosisRequestDAO.class), eq(1));

        mockMvc.perform(get("/diagnosisRequestController/diagnosisRequest/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(DiagnosisRequestDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testUpdateDiagnosisRequestSuccess() throws Exception {
        when(objectFacade.updateObject(any(DiagnosisRequestDAO.class), eq(1))).thenReturn(diagnosisRequest);

        mockMvc.perform(put("/diagnosisRequestController/diagnosisRequest/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "diagnosisRequest": {
                                    "id": 0,
                                    "idChart": 2,
                                    "idDoctor": 3,
                                    "diagnosis": "",
                                    "idDisease": 0,
                                    "voiceDiagnosis": "",
                                    "creationDate": "",
                                    "modificationDate": ""
                                  }
                                }"""))
                .andExpect(status().is(209))
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idChart").value(diagnosisRequest.getIdChart()))
                .andExpect(jsonPath("$.containedObject.idDoctor").value(diagnosisRequest.getIdDoctor()))
                .andExpect(jsonPath("$.containedObject.diagnosis").value(diagnosisRequest.getDiagnosis()))
                .andExpect(jsonPath("$.containedObject.idDisease").value(diagnosisRequest.getIdDisease()))
                .andExpect(jsonPath("$.containedObject.voiceDiagnosis").value(diagnosisRequest.getVoiceDiagnosis()))
                .andExpect(jsonPath("$.containedObject.creationDate").value(diagnosisRequest.getCreationDate()))
                .andExpect(jsonPath("$.containedObject.modificationDate").value(diagnosisRequest.getModificationDate()));

        verify(objectFacade, times(1)).updateObject(any(DiagnosisRequestDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testUpdateDiagnosisRequestNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(DiagnosisRequestDAO.class), eq(1));

        mockMvc.perform(put("/diagnosisRequestController/diagnosisRequest/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "diagnosisRequest": {
                                    "id": 0,
                                    "idChart": 2,
                                    "idDoctor": 3,
                                    "diagnosis": "",
                                    "idDisease": 0,
                                    "voiceDiagnosis": "",
                                    "creationDate": "",
                                    "modificationDate": ""
                                  }
                                }"""))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).updateObject(any(DiagnosisRequestDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testDeleteDiagnosisRequestSuccess() throws Exception {

        mockMvc.perform(delete("/diagnosisRequestController/diagnosisRequest/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(210));

        verify(objectFacade, times(1)).deleteObject(any(DiagnosisRequestDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteDiagnosisRequestNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(DiagnosisRequestDAO.class), eq(1));

        mockMvc.perform(delete("/diagnosisRequestController/diagnosisRequest/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).deleteObject(any(DiagnosisRequestDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteDiagnosisRequestDeleteError() throws Exception {
        doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(DiagnosisRequestDAO.class), eq(1));

        mockMvc.perform(delete("/diagnosisRequestController/diagnosisRequest/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(455));
        verify(objectFacade, times(1)).deleteObject(any(DiagnosisRequestDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
