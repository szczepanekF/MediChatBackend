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
    import pl.logic.site.model.dao.DoctorDAO;
    import pl.logic.site.model.enums.LogType;
    import pl.logic.site.model.exception.DeleteError;
    import pl.logic.site.model.exception.EntityNotFound;
    import pl.logic.site.model.exception.SaveError;
    import pl.logic.site.model.mysql.Doctor;
    import pl.logic.site.service.LoggingService;

    import java.util.Date;
    import java.util.List;

    @ExtendWith(SpringExtension.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    public class DoctorControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ObjectFacade objectFacade;

        @MockBean
        private LoggingService loggingService;


        private Doctor doctor;
        private Doctor doctor2;

        @BeforeEach
        public void setup() {
            doctor = new Doctor(1,"doc", "sur", new Date(), 0, 0);
            doctor2 = new Doctor(2,"doc2", "sur2", new Date(), 1, 1);
        }

        @Test
        public void testCreateDoctorSuccess() throws Exception {
            when(objectFacade.createObject(any(DoctorDAO.class))).thenReturn(doctor);

            mockMvc.perform(post("/doctorController/doctor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "doctor": {
                                        "id": 0,
                                        "name": "doc",
                                        "surname": "sur",
                                        "birth_date": "",
                                        "specialisation_id": 0,
                                        "isBot": 0
                                      }
                                    }""")).andExpect(status().isCreated())
                    .andExpect(jsonPath("$.containedObject.id").value("1"))
                    .andExpect(jsonPath("$.containedObject.name").value(doctor.getName()))
                    .andExpect(jsonPath("$.containedObject.surname").value(doctor.getSurname()))
                    .andExpect(jsonPath("$.containedObject.birth_date").value(doctor.getBirth_date()))
                    .andExpect(jsonPath("$.containedObject.specialisation_id").value(doctor.getSpecialisation_id()))
                    .andExpect(jsonPath("$.containedObject.isBot").value(doctor.getIsBot()));
            verify(objectFacade, times(1)).createObject(any(DoctorDAO.class));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

        }

        @Test
        public void testCreateDoctorSaveError() throws Exception {
            doThrow(new SaveError("Error saving entity")).when(objectFacade).createObject(any(DoctorDAO.class));

            mockMvc.perform(post("/doctorController/doctor")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "doctor": {
                                        "id": 0,
                                        "name": "doc",
                                        "surname": "sur",
                                        "birth_date": "",
                                        "specialisation_id": 0,
                                        "isBot": 0
                                      }
                                    }"""))
                    .andExpect(status().is(453));
            verify(objectFacade, times(1)).createObject(any(DoctorDAO.class));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());


        }

        @Test
        public void testGetAllDoctorsByFilterSuccess() throws Exception {
            List<Doctor> doctors = List.of(doctor, doctor2);
            when(objectFacade.getObjects(any(DoctorDAO.class), eq(2))).thenReturn(doctors);

            mockMvc.perform(get("/doctorController/getDoctors/" + 2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                    .andExpect(jsonPath("$.containedObject[0].name").value(doctor.getName()))
                    .andExpect(jsonPath("$.containedObject[0].surname").value(doctor.getSurname()))
                    .andExpect(jsonPath("$.containedObject[1].birth_date").value(doctor2.getBirth_date()))
                    .andExpect(jsonPath("$.containedObject[1].specialisation_id").value(doctor2.getSpecialisation_id()))
                    .andExpect(jsonPath("$.containedObject[1].isBot").value(doctor2.getIsBot()));
            verify(objectFacade, times(1)).getObjects(any(DoctorDAO.class), eq(2));
            verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

        }

        @Test
        public void testGetRealDoctorsByFilterSuccess() throws Exception {
            List<Doctor> doctors = List.of(doctor);
            when(objectFacade.getObjects(any(DoctorDAO.class), eq(0))).thenReturn(doctors);

            mockMvc.perform(get("/doctorController/getDoctors/" + 0)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.containedObject[0].id").value("1"))
                    .andExpect(jsonPath("$.containedObject[0].name").value(doctor.getName()))
                    .andExpect(jsonPath("$.containedObject[0].surname").value(doctor.getSurname()))
                    .andExpect(jsonPath("$.containedObject[0].birth_date").value(doctor.getBirth_date()))
                    .andExpect(jsonPath("$.containedObject[0].specialisation_id").value(doctor.getSpecialisation_id()))
                    .andExpect(jsonPath("$.containedObject[0].isBot").value(doctor.getIsBot()));
            verify(objectFacade, times(1)).getObjects(any(DoctorDAO.class), eq(0));
            verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

        }
        @Test
        public void testGetBotDoctorsByFilterSuccess() throws Exception {
            List<Doctor> doctors = List.of(doctor2);
            when(objectFacade.getObjects(any(DoctorDAO.class), eq(1))).thenReturn(doctors);

            mockMvc.perform(get("/doctorController/getDoctors/" + 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.containedObject[0].id").value(doctor2.getId()))
                    .andExpect(jsonPath("$.containedObject[0].name").value(doctor2.getName()))
                    .andExpect(jsonPath("$.containedObject[0].surname").value(doctor2.getSurname()))
                    .andExpect(jsonPath("$.containedObject[0].birth_date").value(doctor2.getBirth_date()))
                    .andExpect(jsonPath("$.containedObject[0].specialisation_id").value(doctor2.getSpecialisation_id()))
                    .andExpect(jsonPath("$.containedObject[0].isBot").value(doctor2.getIsBot()));
            verify(objectFacade, times(1)).getObjects(any(DoctorDAO.class), eq(1));
            verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

        }
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2})
        public void testGetDoctorsByFilterNotFound(int filter) throws Exception {
            doThrow(new EntityNotFound("Not found")).when(objectFacade).getObjects(any(DoctorDAO.class), eq(filter));

            mockMvc.perform(get("/doctorController/getDoctors/" + filter)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            verify(objectFacade, times(1)).getObjects(any(DoctorDAO.class), eq(filter));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

        }

        @Test
        public void testGetDoctorByDiagnosisSuccess() throws Exception {
            when(objectFacade.getDoctorByDiagnosisRequest(1)).thenReturn(doctor);

            mockMvc.perform(get("/doctorController/getDoctorByDiagnosis/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.containedObject.id").value("1"))
                    .andExpect(jsonPath("$.containedObject.name").value(doctor.getName()))
                    .andExpect(jsonPath("$.containedObject.surname").value(doctor.getSurname()))
                    .andExpect(jsonPath("$.containedObject.birth_date").value(doctor.getBirth_date()))
                    .andExpect(jsonPath("$.containedObject.specialisation_id").value(doctor.getSpecialisation_id()))
                    .andExpect(jsonPath("$.containedObject.isBot").value(doctor.getIsBot()));

            verify(objectFacade, times(1)).getDoctorByDiagnosisRequest(1);
            verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

        }

        @Test
        public void testGetDoctorByDiagnosisNotFound() throws Exception {
            doThrow(new EntityNotFound("Not found")).when(objectFacade).getDoctorByDiagnosisRequest(1);

            mockMvc.perform(get("/doctorController/getDoctorByDiagnosis/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            verify(objectFacade, times(1)).getDoctorByDiagnosisRequest(1);
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

        }


        @Test
        public void testGetDoctorSuccess() throws Exception {
            when(objectFacade.getObject(any(DoctorDAO.class), eq(1))).thenReturn(doctor);

            mockMvc.perform(get("/doctorController/doctors/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.containedObject.id").value("1"))
                    .andExpect(jsonPath("$.containedObject.name").value(doctor.getName()))
                    .andExpect(jsonPath("$.containedObject.surname").value(doctor.getSurname()))
                    .andExpect(jsonPath("$.containedObject.birth_date").value(doctor.getBirth_date()))
                    .andExpect(jsonPath("$.containedObject.specialisation_id").value(doctor.getSpecialisation_id()))
                    .andExpect(jsonPath("$.containedObject.isBot").value(doctor.getIsBot()));

            verify(objectFacade, times(1)).getObject(any(DoctorDAO.class), eq(1));
            verify(loggingService, never()).createLog(anyString(), any(), any(LogType.class), any());

        }

        @Test
        public void testGetDoctorNotFound() throws Exception {
            doThrow(new EntityNotFound("Not found")).when(objectFacade).getObject(any(DoctorDAO.class), eq(1));

            mockMvc.perform(get("/doctorController/doctors/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            verify(objectFacade, times(1)).getObject(any(DoctorDAO.class), eq(1));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

        }


        @Test
        public void testUpdateDoctorSuccess() throws Exception {
            when(objectFacade.updateObject(any(DoctorDAO.class), eq(1))).thenReturn(doctor);

            mockMvc.perform(put("/doctorController/doctors/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "doctor": {
                                        "id": 0,
                                        "name": "doc",
                                        "surname": "sur",
                                        "birth_date": "",
                                        "specialisation_id": 0,
                                        "isBot": 0
                                      }
                                    }"""))
                    .andExpect(jsonPath("$.containedObject.id").value("1"))
                    .andExpect(jsonPath("$.containedObject.name").value(doctor.getName()))
                    .andExpect(jsonPath("$.containedObject.surname").value(doctor.getSurname()))
                    .andExpect(jsonPath("$.containedObject.birth_date").value(doctor.getBirth_date()))
                    .andExpect(jsonPath("$.containedObject.specialisation_id").value(doctor.getSpecialisation_id()))
                    .andExpect(jsonPath("$.containedObject.isBot").value(doctor.getIsBot()));

            verify(objectFacade, times(1)).updateObject(any(DoctorDAO.class), eq(1));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
        }

        @Test
        public void testUpdateDoctorNotFound() throws Exception {
            doThrow(new EntityNotFound("Not found")).when(objectFacade).updateObject(any(DoctorDAO.class), eq(1));

            mockMvc.perform(put("/doctorController/doctors/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "doctor": {
                                        "id": 0,
                                        "name": "doc",
                                        "surname": "sur",
                                        "birth_date": "",
                                        "specialisation_id": 0,
                                        "isBot": 0
                                      }
                                    }"""))
                    .andExpect(status().isNotFound());
            verify(objectFacade, times(1)).updateObject(any(DoctorDAO.class), eq(1));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

        }


        @Test
        public void testDeleteDoctorSuccess() throws Exception {

            mockMvc.perform(delete("/doctorController/doctors/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(210));

            verify(objectFacade, times(1)).deleteObject(any(DoctorDAO.class), eq(1));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

        }

        @Test
        public void testDeleteDoctorNotFound() throws Exception {
            doThrow(new EntityNotFound("Not found")).when(objectFacade).deleteObject(any(DoctorDAO.class), eq(1));

            mockMvc.perform(delete("/doctorController/doctors/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            verify(objectFacade, times(1)).deleteObject(any(DoctorDAO.class), eq(1));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());

        }

        @Test
        public void testDeleteDoctorDeleteError() throws Exception {
            doThrow(new DeleteError("Error deleting entity")).when(objectFacade).deleteObject(any(DoctorDAO.class), eq(1));

            mockMvc.perform(delete("/doctorController/doctors/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(455));
            verify(objectFacade, times(1)).deleteObject(any(DoctorDAO.class), eq(1));
            verify(loggingService, times(1)).createLog(anyString(), any(), any(LogType.class), any());
        }
    }
