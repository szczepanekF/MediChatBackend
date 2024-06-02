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
import pl.logic.site.model.dao.DiseaseSymptomDAO;
import pl.logic.site.model.enums.LogType;

import pl.logic.site.model.exception.EntityNotFound;

import pl.logic.site.model.mysql.DiseaseSymptom;
import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DiseaseSymptomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private DiseaseSymptom diseaseSymptom;
    private DiseaseSymptom diseaseSymptom2;

    @BeforeEach
    public void setup() {
        diseaseSymptom = new DiseaseSymptom(1, 2, 3);
        diseaseSymptom2 = new DiseaseSymptom(2, 3, 2);
    }

    @Test
    public void testGetAllDiseaseSymptomsSuccess() throws Exception {
        when(objectFacade.getObjects(any(DiseaseSymptomDAO.class), eq(-1))).thenReturn(List.of(diseaseSymptom, diseaseSymptom2));

        mockMvc.perform(get("/diseaseSymptomController/diseaseSymptoms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value(diseaseSymptom.getId()))
                .andExpect(jsonPath("$.containedObject[0].idDisease").value(diseaseSymptom.getIdDisease()))
                .andExpect(jsonPath("$.containedObject[1].idSymptom").value(diseaseSymptom2.getIdSymptom()));
        verify(objectFacade, times(1)).getObjects(any(DiseaseSymptomDAO.class), eq(-1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetAllDiseaseSymptomsNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(DiseaseSymptomDAO.class), eq(-1));

        mockMvc.perform(get("/diseaseSymptomController/diseaseSymptoms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(DiseaseSymptomDAO.class), eq(-1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetDiseaseSymptomSuccess() throws Exception {
        when(objectFacade.getObject(any(DiseaseSymptomDAO.class), eq(diseaseSymptom.getId()))).thenReturn(diseaseSymptom);

        mockMvc.perform(get("/diseaseSymptomController/diseaseSymptom/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idDisease").value(diseaseSymptom.getIdDisease()))
                .andExpect(jsonPath("$.containedObject.idSymptom").value(diseaseSymptom.getIdSymptom()));

        verify(objectFacade, times(1)).getObject(any(DiseaseSymptomDAO.class), eq(diseaseSymptom.getId()));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetDiseaseSymptomNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(DiseaseSymptomDAO.class), eq(diseaseSymptom.getId()));

        mockMvc.perform(get("/diseaseSymptomController/diseaseSymptom/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(DiseaseSymptomDAO.class), eq(diseaseSymptom.getId()));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
