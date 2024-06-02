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
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.service.LoggingService;

import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private Patient patient;
    private Patient patient2;

    @BeforeEach
    public void setup() {
        patient = new Patient(1,"patient", "sur", new Date(), 195, 80, "male", Status.OFFLINE, "cm", "kg");
        patient2 = new Patient(2,"patient2", "sur2", new Date(), 150, 30, "male", Status.OFFLINE, "cm", "kg");
    }

    @Test
    public void testCreatePatientSuccess() throws Exception {
        when(objectFacade.createObject(any(PatientDAO.class))).thenReturn(patient);

        mockMvc.perform(post("/patientController/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patient": {
                                    "id": 0,
                                    "name": "patient",
                                    "surname": "sur",
                                    "birth_date": "",
                                    "height": 195,
                                    "weight": 80,
                                    "gender": "male",
                                    "status": "OFFLINE",
                                    "heightUnit": "cm",
                                    "weightUnit": "kg"
                                  }
                                }""")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.name").value(patient.getName()))
                .andExpect(jsonPath("$.containedObject.surname").value(patient.getSurname()))
                .andExpect(jsonPath("$.containedObject.birth_date").value(patient.getBirth_date()))
                .andExpect(jsonPath("$.containedObject.height").value(patient.getHeight()))
                .andExpect(jsonPath("$.containedObject.weight").value(patient.getWeight()))
                .andExpect(jsonPath("$.containedObject.gender").value(patient.getGender()))
                .andExpect(jsonPath("$.containedObject.status").value(patient.getStatus().name()))
                .andExpect(jsonPath("$.containedObject.heightUnit").value(patient.getHeightUnit()))
                .andExpect(jsonPath("$.containedObject.weightUnit").value(patient.getWeightUnit()));
        verify(objectFacade, times(1)).createObject(any(PatientDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testCreatePatientSaveError() throws Exception {
        doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(PatientDAO.class));

        mockMvc.perform(post("/patientController/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patient": {
                                    "id": 0,
                                    "name": "patient",
                                    "surname": "sur",
                                    "birth_date": "",
                                    "height": 195,
                                    "weight": 80,
                                    "gender": "male",
                                    "status": "OFFLINE",
                                    "heightUnit": "cm",
                                    "weightUnit": "kg"
                                  }
                                }"""))
                .andExpect(status().is(453));
        verify(objectFacade, times(1)).createObject(any(PatientDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());


    }

    @Test
    public void testGetAllPatientsSuccess() throws Exception {
        List<Patient> patients = List.of(patient, patient2);
        when(objectFacade.getObjects(any(PatientDAO.class), eq(-1))).thenReturn(patients);

        mockMvc.perform(get("/patientController/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].name").value(patient.getName()))
                .andExpect(jsonPath("$.containedObject[0].surname").value(patient.getSurname()))
                .andExpect(jsonPath("$.containedObject[0].birth_date").value(patient.getBirth_date()))
                .andExpect(jsonPath("$.containedObject[0].height").value(patient.getHeight()))
                .andExpect(jsonPath("$.containedObject[1].weight").value(patient2.getWeight()))
                .andExpect(jsonPath("$.containedObject[1].gender").value(patient2.getGender()))
                .andExpect(jsonPath("$.containedObject[1].status").value(patient2.getStatus().name()))
                .andExpect(jsonPath("$.containedObject[1].heightUnit").value(patient2.getHeightUnit()))
                .andExpect(jsonPath("$.containedObject[1].weightUnit").value(patient2.getWeightUnit()));
        verify(objectFacade, times(1)).getObjects(any(PatientDAO.class), eq(-1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }



    @Test
    public void testGetAllPatientsNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(PatientDAO.class), eq(-1));

        mockMvc.perform(get("/patientController/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(PatientDAO.class), eq(-1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetPatientByDoctorIdSuccess() throws Exception {
        List<Patient> patients = List.of(patient, patient2);

        when(objectFacade.getObjects(any(PatientDAO.class), eq(1))).thenReturn(patients);


        mockMvc.perform(get("/patientController/patientsByDoctor/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].name").value(patient.getName()))
                .andExpect(jsonPath("$.containedObject[0].surname").value(patient.getSurname()))
                .andExpect(jsonPath("$.containedObject[0].birth_date").value(patient.getBirth_date()))
                .andExpect(jsonPath("$.containedObject[0].height").value(patient.getHeight()))
                .andExpect(jsonPath("$.containedObject[1].weight").value(patient2.getWeight()))
                .andExpect(jsonPath("$.containedObject[1].gender").value(patient2.getGender()))
                .andExpect(jsonPath("$.containedObject[1].status").value(patient2.getStatus().name()))
                .andExpect(jsonPath("$.containedObject[1].heightUnit").value(patient2.getHeightUnit()))
                .andExpect(jsonPath("$.containedObject[1].weightUnit").value(patient2.getWeightUnit()));

        verify(objectFacade, times(1)).getObjects(any(PatientDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetPatientByDoctorIdNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(PatientDAO.class), eq(1));

        mockMvc.perform(get("/patientController/patientsByDoctor/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(PatientDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testGetPatientSuccess() throws Exception {
        when(objectFacade.getObject(any(PatientDAO.class), eq(1))).thenReturn(patient);

        mockMvc.perform(get("/patientController/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.name").value(patient.getName()))
                .andExpect(jsonPath("$.containedObject.surname").value(patient.getSurname()))
                .andExpect(jsonPath("$.containedObject.birth_date").value(patient.getBirth_date()))
                .andExpect(jsonPath("$.containedObject.height").value(patient.getHeight()))
                .andExpect(jsonPath("$.containedObject.weight").value(patient.getWeight()))
                .andExpect(jsonPath("$.containedObject.gender").value(patient.getGender()))
                .andExpect(jsonPath("$.containedObject.status").value(patient.getStatus().name()))
                .andExpect(jsonPath("$.containedObject.heightUnit").value(patient.getHeightUnit()))
                .andExpect(jsonPath("$.containedObject.weightUnit").value(patient.getWeightUnit()));

        verify(objectFacade, times(1)).getObject(any(PatientDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetPatientNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(PatientDAO.class), eq(1));

        mockMvc.perform(get("/patientController/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(PatientDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testUpdatePatientSuccess() throws Exception {
        when(objectFacade.updateObject(any(PatientDAO.class), eq(1))).thenReturn(patient);

        mockMvc.perform(put("/patientController/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patient": {
                                    "id": 1,
                                    "name": "patient",
                                    "surname": "sur",
                                    "birth_date": "",
                                    "height": 195,
                                    "weight": 80,
                                    "gender": "male",
                                    "status": "OFFLINE",
                                    "heightUnit": "cm",
                                    "weightUnit": "kg"
                                  }
                                }"""))
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.name").value(patient.getName()))
                .andExpect(jsonPath("$.containedObject.surname").value(patient.getSurname()))
                .andExpect(jsonPath("$.containedObject.birth_date").value(patient.getBirth_date()))
                .andExpect(jsonPath("$.containedObject.height").value(patient.getHeight()))
                .andExpect(jsonPath("$.containedObject.weight").value(patient.getWeight()))
                .andExpect(jsonPath("$.containedObject.gender").value(patient.getGender()))
                .andExpect(jsonPath("$.containedObject.status").value(patient.getStatus().name()))
                .andExpect(jsonPath("$.containedObject.heightUnit").value(patient.getHeightUnit()))
                .andExpect(jsonPath("$.containedObject.weightUnit").value(patient.getWeightUnit()));

        verify(objectFacade, times(1)).updateObject(any(PatientDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testUpdatePatientNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(PatientDAO.class), eq(1));

        mockMvc.perform(put("/patientController/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patient": {
                                    "id": 1,
                                    "name": "patient",
                                    "surname": "sur",
                                    "birth_date": "",
                                    "height": 195,
                                    "weight": 80,
                                    "gender": "male",
                                    "status": "OFFLINE",
                                    "heightUnit": "cm",
                                    "weightUnit": "kg"
                                  }
                                }"""))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).updateObject(any(PatientDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testDeletePatientSuccess() throws Exception {

        mockMvc.perform(delete("/patientController/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(210));

        verify(objectFacade, times(1)).deleteObject(any(PatientDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeletePatientNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(PatientDAO.class), eq(1));

        mockMvc.perform(delete("/patientController/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).deleteObject(any(PatientDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeletePatientDeleteError() throws Exception {
        doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(PatientDAO.class), eq(1));

        mockMvc.perform(delete("/patientController/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(455));
        verify(objectFacade, times(1)).deleteObject(any(PatientDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
