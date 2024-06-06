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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Chart;

import pl.logic.site.service.LoggingService;

import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ChartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private Chart chart;
    private Chart chart2;

    @BeforeEach
    public void setup() {
        chart = new Chart(1, 2, new Date());
        chart2 = new Chart(2, 3, new Date());
    }

    @Test
    public void testCreateChartSuccess() throws Exception {
        when(objectFacade.createObject(any(ChartDAO.class))).thenReturn(chart);

        mockMvc.perform(post("/chartController/chart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chart": {
                                    "id": 0,
                                    "idPatient": 2
                                }}""")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idPatient").value(chart.getIdPatient()));

        verify(objectFacade, times(1)).createObject(any(ChartDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testCreateChartSaveError() throws Exception {
        doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(ChartDAO.class));

        mockMvc.perform(post("/chartController/chart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chart": {
                                    "id": 3,
                                    "idPatient": 2
                                }}"""))
                .andExpect(status().is(453));
        verify(objectFacade, times(1)).createObject(any(ChartDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());


    }

    @Test
    public void testGetChartsForPatientSuccess() throws Exception {
        List<Chart> charts = List.of(chart, chart2);
        when(objectFacade.getObjects(any(ChartDAO.class), eq(1))).thenReturn(charts);

        mockMvc.perform(get("/chartController/charts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].idPatient").value(chart.getIdPatient()))
                .andExpect(jsonPath("$.containedObject[1].id").value("2"))
                .andExpect(jsonPath("$.containedObject[1].idPatient").value(chart2.getIdPatient()));

        verify(objectFacade, times(1)).getObjects(any(ChartDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetChartsForPatientNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(ChartDAO.class), eq(1));

        mockMvc.perform(get("/chartController/charts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(ChartDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    public void testGetChartsByStateForPatientSuccess(int state) throws Exception {
        List<Chart> charts = List.of(chart, chart2);
        when(objectFacade.getChartsByStateAndPatientId(state, 1)).thenReturn(charts);

        mockMvc.perform(get("/chartController/chartsByState/" + state + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].idPatient").value(chart.getIdPatient()))
                .andExpect(jsonPath("$.containedObject[1].id").value("2"))
                .andExpect(jsonPath("$.containedObject[1].idPatient").value(chart2.getIdPatient()));
        verify(objectFacade, times(1)).getChartsByStateAndPatientId(state, 1);
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    public void testGetChartsByStateForPatientNotFound(int state) throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getChartsByStateAndPatientId(state, 1);

        mockMvc.perform(get("/chartController/chartsByState/" + state + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getChartsByStateAndPatientId(state, 1);
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetChartSuccess() throws Exception {
        when(objectFacade.getObject(any(ChartDAO.class), eq(1))).thenReturn(chart);

        mockMvc.perform(get("/chartController/chart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idPatient").value(chart.getIdPatient()));

        verify(objectFacade, times(1)).getObject(any(ChartDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetChartNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(ChartDAO.class), eq(1));

        mockMvc.perform(get("/chartController/chart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(ChartDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testUpdateChartSuccess() throws Exception {
        when(objectFacade.updateObject(any(ChartDAO.class), eq(1))).thenReturn(chart);

        mockMvc.perform(put("/chartController/chart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chart": {
                                    "id": 1,
                                    "idPatient": 2
                                }}"""))
                .andExpect(status().is(209))
                .andExpect(jsonPath("$.containedObject.id").value(chart.getId()))
                .andExpect(jsonPath("$.containedObject.idPatient").value(chart.getIdPatient()));

        verify(objectFacade, times(1)).updateObject(any(ChartDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testUpdateChartNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(ChartDAO.class), eq(1));

        mockMvc.perform(put("/chartController/chart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chart": {
                                    "id": 0,
                                    "idPatient": 2
                                }}"""))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).updateObject(any(ChartDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testDeleteChartSuccess() throws Exception {

        mockMvc.perform(delete("/chartController/chart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(210));

        verify(objectFacade, times(1)).deleteObject(any(ChartDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteChartNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(ChartDAO.class), eq(1));

        mockMvc.perform(delete("/chartController/chart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).deleteObject(any(ChartDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteChartDeleteError() throws Exception {
        doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(ChartDAO.class), eq(1));

        mockMvc.perform(delete("/chartController/chart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(455));
        verify(objectFacade, times(1)).deleteObject(any(ChartDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
