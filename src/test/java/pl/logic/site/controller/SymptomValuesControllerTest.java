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
import pl.logic.site.model.dao.SymptomValuesDAO;
import pl.logic.site.model.enums.LogType;

import pl.logic.site.model.exception.EntityNotFound;

import pl.logic.site.model.mysql.SymptomValues;
import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SymptomValuesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private SymptomValues symptomValues;
    private SymptomValues symptomValues2;

    @BeforeEach
    public void setup() {
        symptomValues = new SymptomValues(1, 2, "weak", "mid", "hard");
        symptomValues2 = new SymptomValues(2, 1, "less", "avg", "more");

    }


    @Test
    public void testGetSymptomValuesSuccess() throws Exception {
        when(objectFacade.getObjects(any(SymptomValuesDAO.class), eq(-1))).thenReturn(List.of(symptomValues,symptomValues2));

        mockMvc.perform(get("/symptomValuesController/symptomValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].symptomId").value(symptomValues.getSymptomId()))
                .andExpect(jsonPath("$.containedObject[0].symptomValueWeak").value(symptomValues.getSymptomValueWeak()))
                .andExpect(jsonPath("$.containedObject[1].symptomValueAverage").value(symptomValues2.getSymptomValueAverage()))
                .andExpect(jsonPath("$.containedObject[1].symptomValueHard").value(symptomValues2.getSymptomValueHard()));

        verify(objectFacade, times(1)).getObjects(any(SymptomValuesDAO.class), eq(-1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testGetSymptomValuesNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(SymptomValuesDAO.class), eq(-1));

        mockMvc.perform(get("/symptomValuesController/symptomValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(SymptomValuesDAO.class), eq(-1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
