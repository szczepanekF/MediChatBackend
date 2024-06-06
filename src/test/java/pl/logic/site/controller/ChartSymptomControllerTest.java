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
import pl.logic.site.model.dao.ChartSymptomDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.ChartSymptom;

import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ChartSymptomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private ChartSymptom chartSymptom;
    private ChartSymptom chartSymptom2;

    @BeforeEach
    public void setup() {
        chartSymptom = new ChartSymptom(1, 2, 3, "mid");
        chartSymptom2 = new ChartSymptom(1, 3, 2, "avg");
    }

    @Test
    public void testCreateChartSymptomSuccess() throws Exception {
        when(objectFacade.createObject(any(ChartSymptomDAO.class))).thenReturn(chartSymptom);

        mockMvc.perform(post("/chartSymptomController/chartSymptom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chartSymptom": {
                                    "id": 0,
                                    "idChart": 2,
                                    "idSymptom": 3,
                                    "symptomValueLevel": "mid"
                                  }}""")).andExpect(status().isCreated())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.idChart").value(chartSymptom.getIdChart()))
                .andExpect(jsonPath("$.containedObject.idSymptom").value(chartSymptom.getIdSymptom()))
                .andExpect(jsonPath("$.containedObject.symptomValueLevel").value(chartSymptom.getSymptomValueLevel()));

        verify(objectFacade, times(1)).createObject(any(ChartSymptomDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testCreateChartSymptomSaveError() throws Exception {
        doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(ChartSymptomDAO.class));

        mockMvc.perform(post("/chartSymptomController/chartSymptom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chartSymptom": {
                                    "id": 0,
                                    "idChart": 2,
                                    "idSymptom": 3,
                                    "symptomValueLevel": "mid"
                                  }}"""))
                .andExpect(status().is(453));
        verify(objectFacade, times(1)).createObject(any(ChartSymptomDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());


    }

    @Test
    public void testGetChartSymptomsForChartIdSuccess() throws Exception {
        List<ChartSymptom> chartSymptoms = List.of(chartSymptom, chartSymptom2);
        when(objectFacade.getObjects(any(ChartSymptomDAO.class), eq(2))).thenReturn(chartSymptoms);

        mockMvc.perform(get("/chartSymptomController/chartSymptoms/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].idChart").value(chartSymptom.getIdChart()))
                .andExpect(jsonPath("$.containedObject[1].idSymptom").value(chartSymptom2.getIdSymptom()))
                .andExpect(jsonPath("$.containedObject[1].symptomValueLevel").value(chartSymptom2.getSymptomValueLevel()));
        verify(objectFacade, times(1)).getObjects(any(ChartSymptomDAO.class), eq(2));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetChartSymptomsForChartIdNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(ChartSymptomDAO.class), eq(2));

        mockMvc.perform(get("/chartSymptomController/chartSymptoms/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(ChartSymptomDAO.class), eq(2));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testUpdateChartSymptomSuccess() throws Exception {
        when(objectFacade.updateObject(any(ChartSymptomDAO.class), eq(1))).thenReturn(chartSymptom);

        mockMvc.perform(put("/chartSymptomController/chartSymptoms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chartSymptom": {
                                    "id": 1,
                                    "idChart": 2,
                                    "idSymptom": 3,
                                    "symptomValueLevel": "mid"
                                  }}"""))
                .andExpect(status().is(209))
                .andExpect(jsonPath("$.containedObject.idChart").value("2"));

        verify(objectFacade, times(1)).updateObject(any(ChartSymptomDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testUpdateChartSymptomNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(ChartSymptomDAO.class), eq(1));

        mockMvc.perform(put("/chartSymptomController/chartSymptoms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {  "chartSymptom": {
                                    "id": 1,
                                    "idChart": 2,
                                    "idSymptom": 3,
                                    "symptomValueLevel": "mid"
                                  }}"""))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).updateObject(any(ChartSymptomDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testDeleteChartSymptomSuccess() throws Exception {

        mockMvc.perform(delete("/chartSymptomController/chartSymptoms/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(210));

        verify(objectFacade, times(1)).deleteObject(any(ChartSymptomDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteChartSymptomNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(ChartSymptomDAO.class), eq(1));

        mockMvc.perform(delete("/chartSymptomController/chartSymptoms/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).deleteObject(any(ChartSymptomDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteChartSymptomDeleteError() throws Exception {
        doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(ChartSymptomDAO.class), eq(1));

        mockMvc.perform(delete("/chartSymptomController/chartSymptoms/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(455));
        verify(objectFacade, times(1)).deleteObject(any(ChartSymptomDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
