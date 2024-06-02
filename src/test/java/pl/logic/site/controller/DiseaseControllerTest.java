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
import pl.logic.site.model.dao.DiseaseDAO;
import pl.logic.site.model.enums.LogType;

import pl.logic.site.model.exception.EntityNotFound;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DiseaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private Disease disease;
    private Disease disease2;

    @BeforeEach
    public void setup() {
        disease = new Disease(1, "name");
        disease2 = new Disease(2, "name2");
    }

    @Test
    public void testGetAllDiseasesSuccess() throws Exception {
        when(objectFacade.getObjects(any(DiseaseDAO.class), eq(-1))).thenReturn(List.of(disease,disease2));

        mockMvc.perform(get("/diseaseController/diseases")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[1].name").value(disease2.getName()));

        verify(objectFacade, times(1)).getObjects(any(DiseaseDAO.class), eq(-1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetAllDiseasesNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(DiseaseDAO.class), eq(-1));

        mockMvc.perform(get("/diseaseController/diseases")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(DiseaseDAO.class), eq(-1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetDiseaseSuccess() throws Exception {
        when(objectFacade.getObject(any(DiseaseDAO.class), eq(disease.getId()))).thenReturn(disease);

        mockMvc.perform(get("/diseaseController/disease/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.name").value(disease.getName()));
        verify(objectFacade, times(1)).getObject(any(DiseaseDAO.class), eq(disease.getId()));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetDiseaseNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(DiseaseDAO.class), eq(disease.getId()));

        mockMvc.perform(get("/diseaseController/disease/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(DiseaseDAO.class), eq(disease.getId()));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
