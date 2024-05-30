package pl.logic.site.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

//import pl.logic.site.facade.UserFacade;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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