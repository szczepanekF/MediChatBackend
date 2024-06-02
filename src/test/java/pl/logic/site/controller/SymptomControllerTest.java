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
import pl.logic.site.model.dao.SymptomDAO;
import pl.logic.site.model.enums.LogType;

import pl.logic.site.model.exception.EntityNotFound;

import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SymptomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private Symptom symptom;
    private Symptom symptom2;

    @BeforeEach
    public void setup() {
        symptom = new Symptom(1, "name");
        symptom2 = new Symptom(2, "name2");
    }

    @Test
    public void testGetAllSymptomsSuccess() throws Exception {
        when(objectFacade.getObjects(any(SymptomDAO.class), eq(-1))).thenReturn(List.of(symptom,symptom2));

        mockMvc.perform(get("/symptomController/symptoms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[1].name").value(symptom2.getName()));

        verify(objectFacade, times(1)).getObjects(any(SymptomDAO.class), eq(-1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetAllSymptomsNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(SymptomDAO.class), eq(-1));

        mockMvc.perform(get("/symptomController/symptoms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(SymptomDAO.class), eq(-1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetSymptomSuccess() throws Exception {
        when(objectFacade.getObject(any(SymptomDAO.class), eq(symptom.getId()))).thenReturn(symptom);

        mockMvc.perform(get("/symptomController/symptoms/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.name").value(symptom.getName()));
        verify(objectFacade, times(1)).getObject(any(SymptomDAO.class), eq(symptom.getId()));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetSymptomNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(SymptomDAO.class), eq(symptom.getId()));

        mockMvc.perform(get("/symptomController/symptoms/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(SymptomDAO.class), eq(symptom.getId()));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
