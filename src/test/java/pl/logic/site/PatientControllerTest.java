package pl.logic.site;

import io.jsonwebtoken.impl.JwtTokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.logic.site.config.JwtAuthFilter;
import pl.logic.site.controller.PatientController;

//import pl.logic.site.facade.UserFacade;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.impl.JwtServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    public void testEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/patientController/patients"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testEndpointWithAuthentication() throws Exception {
        mockMvc.perform(get("/patientController/patients"))
                .andExpect(status().isOk());
    }

    @Test
    void testSingleSuccessTest() {
        assertTrue(true);
    }

}