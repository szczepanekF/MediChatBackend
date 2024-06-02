package pl.logic.site.controller;

import static net.bytebuddy.matcher.ElementMatchers.is;
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
import pl.logic.site.model.dao.ExaminationDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Examination;
import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ExaminationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private Examination examination;

    @BeforeEach
    public void setup() {
        examination = new Examination(1, 2, "blood type", "AB");
    }

    @Test
    public void testCreateExaminationSuccess() throws Exception {
        when(objectFacade.createObject(any(ExaminationDAO.class))).thenReturn(examination);

        mockMvc.perform(post("/examinationController/examination")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "examination": {
                                    "id": 0,
                                    "idPatient": 2,
                                    "examination": "blood type",
                                    "examinationValue": "AB"
                                  }}""")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idPatient").value(examination.getIdPatient()))
                .andExpect(jsonPath("$.containedObject.examination").value(examination.getExamination()))
                .andExpect(jsonPath("$.containedObject.examinationValue").value(examination.getExaminationValue()));

        verify(objectFacade, times(1)).createObject(any(ExaminationDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testCreateExaminationSaveError() throws Exception {
        doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(ExaminationDAO.class));

        mockMvc.perform(post("/examinationController/examination")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "examination": {
                                    "id": 0,
                                    "idPatient": 2,
                                    "examination": "blood type",
                                    "examinationValue": "AB"
                                  }}"""))
                .andExpect(status().is(453));
        verify(objectFacade, times(1)).createObject(any(ExaminationDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());


    }

    @Test
    public void testGetExaminationsForPatientSuccess() throws Exception {
        List<Examination> examinations = List.of(examination);
        when(objectFacade.getObjects(any(ExaminationDAO.class), eq(1))).thenReturn(examinations);

        mockMvc.perform(get("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].idPatient").value(examination.getIdPatient()))
                .andExpect(jsonPath("$.containedObject[0].examination").value(examination.getExamination()))
                .andExpect(jsonPath("$.containedObject[0].examinationValue").value(examination.getExaminationValue()));
        verify(objectFacade, times(1)).getObjects(any(ExaminationDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetExaminationsForPatientNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(get("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(ExaminationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetExaminationSuccess() throws Exception {
        when(objectFacade.getObject(any(ExaminationDAO.class), eq(1))).thenReturn(examination);

        mockMvc.perform(get("/examinationController/examination/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idPatient").value(examination.getIdPatient()))
                .andExpect(jsonPath("$.containedObject.examination").value(examination.getExamination()))
                .andExpect(jsonPath("$.containedObject.examinationValue").value(examination.getExaminationValue()));

        verify(objectFacade, times(1)).getObject(any(ExaminationDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetExaminationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(get("/examinationController/examination/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(ExaminationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testUpdateExaminationSuccess() throws Exception {
        when(objectFacade.updateObject(any(ExaminationDAO.class), eq(1))).thenReturn(examination);

        mockMvc.perform(put("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "examination": {
                                    "id": 1,
                                    "idPatient": 2,
                                    "examination": "blood type",
                                    "examinationValue": "AB"
                                  }}"""))
                .andExpect(status().is(209))
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idPatient").value(examination.getIdPatient()))
                .andExpect(jsonPath("$.containedObject.examination").value(examination.getExamination()))
                .andExpect(jsonPath("$.containedObject.examinationValue").value(examination.getExaminationValue()));

        verify(objectFacade, times(1)).updateObject(any(ExaminationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testUpdateExaminationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(put("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "examination": {
                                    "id": 1,
                                    "idPatient": 3,
                                    "examination": "blood type",
                                    "examinationValue": "AB"
                                  }}"""))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).updateObject(any(ExaminationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testDeleteExaminationSuccess() throws Exception {

        mockMvc.perform(delete("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(210));
        verify(objectFacade, times(1)).deleteObject(any(ExaminationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteExaminationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(delete("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).deleteObject(any(ExaminationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteExaminationDeleteError() throws Exception {
        doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(delete("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(455));
        verify(objectFacade, times(1)).deleteObject(any(ExaminationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
