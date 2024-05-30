package pl.logic.site.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.ExaminationDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Examination;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.utils.Consts;

import java.util.Arrays;
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

    private ExaminationDAO examinationDAO;
    private Examination examination;

    @BeforeEach
    public void setup() {
        examination = new Examination(1,2,"blood type","AB");

        examinationDAO = new ExaminationDAO(examination);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    public void testCreateExaminationSuccess() throws Exception {
        when(objectFacade.createObject(any(ExaminationDAO.class))).thenReturn(examination);

        mockMvc.perform(post("/examinationController/examination")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{  \"examination\": {\n" +
                                "    \"id\": 0,\n" +
                                "    \"idPatient\": 2,\n" +
                                "    \"examination\": \"blood type\",\n" +
                                "    \"examinationValue\": \"AB\"\n" +
                                "  }}"))
                .andExpect(status().isCreated());
    }

    @WithAnonymousUser
    @Test
    public void testCreateExaminationSaveError() throws Exception {
        doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(ExaminationDAO.class));

        mockMvc.perform(post("/examinationController/examination")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{  \"examination\": {\n" +
                                "    \"id\": 0,\n" +
                                "    \"idPatient\": 2,\n" +
                                "    \"examination\": \"blood type\",\n" +
                                "    \"examinationValue\": \"AB\"\n" +
                                "  }}"))
                .andExpect(status().is(453));
    }

    @WithAnonymousUser
    @Test
    public void testGetAllExaminationsSuccess() throws Exception {
        List<Examination> examinations = Arrays.asList(examination);
        when(objectFacade.getObjects(any(ExaminationDAO.class), eq(1))).thenReturn(examinations);

        mockMvc.perform(get("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    public void testGetAllExaminationsNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(get("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithAnonymousUser
    @Test
    public void testUpdateExaminationSuccess() throws Exception {
        when(objectFacade.updateObject(any(ExaminationDAO.class), eq(1))).thenReturn(examination);

        mockMvc.perform(put("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{  \"examination\": {\n" +
                                "    \"id\": 1,\n" +
                                "    \"idPatient\": 3,\n" +
                                "    \"examination\": \"blood type\",\n" +
                                "    \"examinationValue\": \"AB\"\n" +
                                "  }}"))
                .andExpect(status().is(209));
    }

    @WithAnonymousUser
    @Test
    public void testUpdateExaminationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(put("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{  \"examination\": {\n" +
                                "    \"id\": 1,\n" +
                                "    \"idPatient\": 3,\n" +
                                "    \"examination\": \"blood type\",\n" +
                                "    \"examinationValue\": \"AB\"\n" +
                                "  }}"))
                .andExpect(status().isNotFound());
    }

    @WithAnonymousUser
    @Test
    public void testDeleteExaminationSuccess() throws Exception {
        mockMvc.perform(delete("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(210));
    }

    @WithAnonymousUser
    @Test
    public void testDeleteExaminationNotFound() throws Exception {
        doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(delete("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    public void testDeleteExaminationDeleteError() throws Exception {
        doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(ExaminationDAO.class), eq(1));

        mockMvc.perform(delete("/examinationController/examinations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(455));
    }
}
