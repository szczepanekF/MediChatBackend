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
import pl.logic.site.model.dao.DictionaryExaminationDAO;
import pl.logic.site.model.enums.LogType;

import pl.logic.site.model.exception.EntityNotFound;

import pl.logic.site.model.mysql.DictionaryExamination;
import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DictionaryExaminationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private DictionaryExamination dictionaryExamination;
    private DictionaryExamination dictionaryExamination2;

    @BeforeEach
    public void setup() {
        dictionaryExamination = new DictionaryExamination(1, "name", 2, "value");
        dictionaryExamination2 = new DictionaryExamination(2, "name2", 3, "value2");

    }

    @Test
    public void testGetAllDictionaryExaminationsSuccess() throws Exception {
        when(objectFacade.getObjects(any(DictionaryExaminationDAO.class), eq(-1))).thenReturn(List.of(dictionaryExamination,dictionaryExamination2));

        mockMvc.perform(get("/dictionaryExaminationController/dictionaryExaminations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].examinationName").value(dictionaryExamination.getExaminationName()))
                .andExpect(jsonPath("$.containedObject[1].idDisease").value(dictionaryExamination2.getIdDisease()))
                .andExpect(jsonPath("$.containedObject[1].examinationRequiredValue").value(dictionaryExamination2.getExaminationRequiredValue()));

        verify(objectFacade, times(1)).getObjects(any(DictionaryExaminationDAO.class), eq(-1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetAllDictionaryExaminationsNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(DictionaryExaminationDAO.class), eq(-1));

        mockMvc.perform(get("/dictionaryExaminationController/dictionaryExaminations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(DictionaryExaminationDAO.class), eq(-1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetDictionaryExaminationSuccess() throws Exception {
        when(objectFacade.getObject(any(DictionaryExaminationDAO.class), eq(dictionaryExamination.getId()))).thenReturn(dictionaryExamination);

        mockMvc.perform(get("/dictionaryExaminationController/dictionaryExamination/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.examinationName").value(dictionaryExamination.getExaminationName()))
                .andExpect(jsonPath("$.containedObject.idDisease").value(dictionaryExamination.getIdDisease()))
                .andExpect(jsonPath("$.containedObject.examinationRequiredValue").value(dictionaryExamination.getExaminationRequiredValue()));

        verify(objectFacade, times(1)).getObject(any(DictionaryExaminationDAO.class), eq(dictionaryExamination.getId()));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetDictionaryExaminationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(DictionaryExaminationDAO.class), eq(dictionaryExamination.getId()));

        mockMvc.perform(get("/dictionaryExaminationController/dictionaryExamination/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(DictionaryExaminationDAO.class), eq(dictionaryExamination.getId()));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
