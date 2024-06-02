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
import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Specialisation;

import pl.logic.site.service.LoggingService;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpecialisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectFacade objectFacade;

    @MockBean
    private LoggingService loggingService;


    private Specialisation specialisation;
    private Specialisation specialisation2;

    @BeforeEach
    public void setup() {
        specialisation = new Specialisation(1,"spec1");
        specialisation2 = new Specialisation(2, "spec2");
    }

    @Test
    public void testCreateSpecialisationSuccess() throws Exception {
        when(objectFacade.createObject(any(SpecialisationDAO.class))).thenReturn(specialisation);

        mockMvc.perform(post("/specialisationController/specialisation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                  "specialisation": {
                                    "id": 0,
                                    "specialisation": "spec1"
                                  }
                                }
                                """)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.specialisation").value(specialisation.getSpecialisation()));

        verify(objectFacade, times(1)).createObject(any(SpecialisationDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testCreateSpecialisationSaveError() throws Exception {
        doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(SpecialisationDAO.class));

        mockMvc.perform(post("/specialisationController/specialisation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                  "specialisation": {
                                    "id": 0,
                                    "specialisation": "spec1"
                                  }
                                }
                                """))
                .andExpect(status().is(453));
        verify(objectFacade, times(1)).createObject(any(SpecialisationDAO.class));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());


    }


    @Test
    public void testGetSpecialisationSuccess() throws Exception {

        when(objectFacade.getObject(any(SpecialisationDAO.class), eq(1))).thenReturn(specialisation);

        mockMvc.perform(get("/specialisationController/specialisations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject.id").value("1"))
                .andExpect(jsonPath("$.containedObject.specialisation").value(specialisation.getSpecialisation()))
                .andExpect(jsonPath("$.containedObject.id").value(specialisation.getId()))
                .andExpect(jsonPath("$.containedObject.specialisation").value(specialisation.getSpecialisation()));
        verify(objectFacade, times(1)).getObject(any(SpecialisationDAO.class), eq(1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetSpecialisationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(SpecialisationDAO.class), eq(1));

        mockMvc.perform(get("/specialisationController/specialisations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObject(any(SpecialisationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetAllSpecialisationsSuccess() throws Exception {
        List<Specialisation> specialisations = List.of(specialisation, specialisation2);
        when(objectFacade.getObjects(any(SpecialisationDAO.class), eq(-1))).thenReturn(specialisations);

        mockMvc.perform(get("/specialisationController/specialisations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                .andExpect(jsonPath("$.containedObject[0].specialisation").value(specialisation.getSpecialisation()))
                .andExpect(jsonPath("$.containedObject[1].id").value(specialisation2.getId()))
                .andExpect(jsonPath("$.containedObject[1].specialisation").value(specialisation2.getSpecialisation()));
        verify(objectFacade, times(1)).getObjects(any(SpecialisationDAO.class), eq(-1));
        verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testGetAllSpecialisationsNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(SpecialisationDAO.class), eq(-1));

        mockMvc.perform(get("/specialisationController/specialisations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).getObjects(any(SpecialisationDAO.class), eq(-1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testUpdateSpecialisationSuccess() throws Exception {
        when(objectFacade.updateObject(any(SpecialisationDAO.class), eq(1))).thenReturn(specialisation);

        mockMvc.perform(put("/specialisationController/specialisations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                  "specialisation": {
                                    "id": 0,
                                    "specialisation": "spec1"
                                  }
                                }
                                """))
                .andExpect(status().is(209))
                .andExpect(jsonPath("$.containedObject.id").value(specialisation.getId()))
                .andExpect(jsonPath("$.containedObject.specialisation").value(specialisation.getSpecialisation()));

        verify(objectFacade, times(1)).updateObject(any(SpecialisationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }

    @Test
    public void testUpdateSpecialisationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(SpecialisationDAO.class), eq(1));

        mockMvc.perform(put("/specialisationController/specialisations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                  "specialisation": {
                                    "id": 0,
                                    "specialisation": "spec1"
                                  }
                                }
                                """))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).updateObject(any(SpecialisationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }


    @Test
    public void testDeleteSpecialisationSuccess() throws Exception {

        mockMvc.perform(delete("/specialisationController/specialisations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(210));

        verify(objectFacade, times(1)).deleteObject(any(SpecialisationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteSpecialisationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(SpecialisationDAO.class), eq(1));

        mockMvc.perform(delete("/specialisationController/specialisations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(objectFacade, times(1)).deleteObject(any(SpecialisationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

    }

    @Test
    public void testDeleteSpecialisationDeleteError() throws Exception {
        doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(SpecialisationDAO.class), eq(1));

        mockMvc.perform(delete("/specialisationController/specialisations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(455));
        verify(objectFacade, times(1)).deleteObject(any(SpecialisationDAO.class), eq(1));
        verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
    }
}
