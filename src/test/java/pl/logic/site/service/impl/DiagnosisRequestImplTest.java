package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.repository.DiagnosisRequestRepository;
import pl.logic.site.service.ChartService;


import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DiagnosisRequestImplTest {
    @Mock
    private ChartService chartService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private MessageServiceImpl messageService;
    @Mock
    private DiagnosisRequestRepository diagnosisRequestRepository;
    @InjectMocks
    private DiagnosisRequestServiceImpl diagnosisRequestService;
    DiagnosisRequest diagnosisRequestWithId0;
    DiagnosisRequest diagnosisRequest2;
    DiagnosisRequest diagnosisRequest3;

    @BeforeEach
    public void setUp() {
        diagnosisRequestWithId0 = new DiagnosisRequest(0, 1, 0, "", 1, "", new Date(), new Date());
        diagnosisRequest2 = new DiagnosisRequest(1, 2, 0, "", 1,"", new Date(), new Date());
        diagnosisRequest3 = new DiagnosisRequest(2, 1, 0, "", 1,"", new Date(), new Date());

    }

    @Test
    void shouldCreateDiagnosisRequest() {
        int patientId = 1;
        when(diagnosisRequestRepository.saveAndFlush(any(DiagnosisRequest.class))).thenReturn(diagnosisRequestWithId0);
        when(chartService.getChart(diagnosisRequestWithId0.getIdChart())).thenReturn(new Chart(0, patientId, new Date()));
        when(userService.findSpringUser(patientId, true)).thenReturn(Optional.of(SpringUser.builder().id(patientId).build()));
        when(userService.findSpringUser(diagnosisRequestWithId0.getIdDoctor(), false)).thenReturn(Optional.of(SpringUser.builder().id(diagnosisRequestWithId0.getIdDoctor()).build()));

        DiagnosisRequestDAO diagnosisRequestDAO = new DiagnosisRequestDAO(diagnosisRequestWithId0);
        DiagnosisRequest savedDiagnosisRequest = diagnosisRequestService.createDiagnosisRequest(diagnosisRequestDAO);

        Assertions.assertEquals(diagnosisRequestWithId0, savedDiagnosisRequest);

        verify(diagnosisRequestRepository, times(1)).saveAndFlush(any(DiagnosisRequest.class));
        verify(chartService, times(1)).getChart(anyInt());
        verify(userService, times(2)).findSpringUser(anyInt(), anyBoolean());
        verify(messageService, times(1)).save(any(Message.class));

    }

    @Test
    void shouldThrowWhileCreatingDiagnosisRequestWithId() {


        DiagnosisRequestDAO diagnosisRequestDAO = new DiagnosisRequestDAO(diagnosisRequest2);
        Assertions.assertThrows(SaveError.class, () -> diagnosisRequestService.createDiagnosisRequest(diagnosisRequestDAO));
        verify(diagnosisRequestRepository, never()).saveAndFlush(any(DiagnosisRequest.class));
        verify(chartService, never()).getChart(anyInt());
        verify(userService, never()).findSpringUser(anyInt(), anyBoolean());
        verify(messageService, never()).save(any(Message.class));
    }


    @Test
    void shouldThrowWhenRepositoryErrorCreatingDiagnosisRequest() {
        doThrow(new NullPointerException()).when(diagnosisRequestRepository).saveAndFlush(any(DiagnosisRequest.class));
        DiagnosisRequestDAO diagnosisRequestDAO = new DiagnosisRequestDAO(diagnosisRequestWithId0);
        Assertions.assertThrows(SaveError.class, () -> diagnosisRequestService.createDiagnosisRequest(diagnosisRequestDAO));
        verify(diagnosisRequestRepository, times(1)).saveAndFlush(any(DiagnosisRequest.class));
        verify(chartService, never()).getChart(anyInt());
        verify(userService, never()).findSpringUser(anyInt(), anyBoolean());
        verify(messageService, never()).save(any(Message.class));
    }

    @Test
    void shouldThrowWhenSendingMessageFailedDuringCreatingDiagnosisRequest() {
        int patientId = 1;
        when(diagnosisRequestRepository.saveAndFlush(any(DiagnosisRequest.class))).thenReturn(diagnosisRequestWithId0);
        when(chartService.getChart(diagnosisRequestWithId0.getIdChart())).thenReturn(new Chart(0, patientId, new Date()));
        when(userService.findSpringUser(patientId, true)).thenReturn(Optional.of(SpringUser.builder().id(patientId).build()));
        when(userService.findSpringUser(diagnosisRequestWithId0.getIdDoctor(), false)).thenReturn(Optional.of(SpringUser.builder().id(diagnosisRequestWithId0.getIdDoctor()).build()));

        doThrow(new NullPointerException()).when(messageService).save(any(Message.class));


        DiagnosisRequestDAO diagnosisRequestDAO = new DiagnosisRequestDAO(diagnosisRequestWithId0);

        Assertions.assertThrows(SaveError.class, () -> diagnosisRequestService.createDiagnosisRequest(diagnosisRequestDAO));
        verify(diagnosisRequestRepository, times(1)).saveAndFlush(any(DiagnosisRequest.class));
        verify(chartService, times(1)).getChart(anyInt());
        verify(userService, times(2)).findSpringUser(anyInt(), anyBoolean());
        verify(messageService, times(1)).save(any(Message.class));

    }

    @Test
    void shouldUpdateDiagnosisRequest() {
        Optional<DiagnosisRequest> diagnosisRequestOptional = Optional.of(diagnosisRequestWithId0);
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(diagnosisRequestOptional);
        when(diagnosisRequestRepository.saveAndFlush(any(DiagnosisRequest.class))).thenReturn(diagnosisRequestWithId0);

        DiagnosisRequestDAO diagnosisRequestDAO = new DiagnosisRequestDAO(diagnosisRequestWithId0);
        DiagnosisRequest savedDiagnosisRequest = diagnosisRequestService.updateDiagnosisRequest(diagnosisRequestDAO, diagnosisRequestWithId0.getId());

        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
        verify(diagnosisRequestRepository, times(1)).saveAndFlush(any(DiagnosisRequest.class));
        Assertions.assertEquals(diagnosisRequestWithId0, savedDiagnosisRequest);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingDiagnosisRequest() {
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(Optional.empty());


        DiagnosisRequestDAO diagnosisRequestDAO = new DiagnosisRequestDAO(diagnosisRequestWithId0);
        Assertions.assertThrows(EntityNotFound.class, () -> diagnosisRequestService.updateDiagnosisRequest(diagnosisRequestDAO, diagnosisRequestWithId0.getId()));
        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
        verify(diagnosisRequestRepository, never()).saveAndFlush(any(DiagnosisRequest.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingDiagnosisRequest() {
        Optional<DiagnosisRequest> diagnosisRequestOptional = Optional.of(diagnosisRequestWithId0);
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(diagnosisRequestOptional);
        doThrow(new NullPointerException()).when(diagnosisRequestRepository).saveAndFlush(any(DiagnosisRequest.class));
        DiagnosisRequestDAO diagnosisRequestDAO = new DiagnosisRequestDAO(diagnosisRequestWithId0);
        Assertions.assertThrows(SaveError.class, () -> diagnosisRequestService.updateDiagnosisRequest(diagnosisRequestDAO, diagnosisRequestWithId0.getId()));
        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
        verify(diagnosisRequestRepository, times(1)).saveAndFlush(any(DiagnosisRequest.class));
    }

    @Test
    void shouldDeleteDiagnosisRequest() {
        Optional<DiagnosisRequest> diagnosisRequestOptional = Optional.of(diagnosisRequestWithId0);
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(diagnosisRequestOptional);

        diagnosisRequestService.deleteDiagnosisRequest(diagnosisRequestWithId0.getId());

        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
        verify(diagnosisRequestRepository, times(1)).deleteById(diagnosisRequestWithId0.getId());
    }

    @Test
    void shouldThrowWhileDeletingNonExistingDiagnosisRequest() {
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFound.class, () -> diagnosisRequestService.deleteDiagnosisRequest(diagnosisRequestWithId0.getId()));
        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
        verify(diagnosisRequestRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenRepositoryErrorDeletingDiagnosisRequest() {
        Optional<DiagnosisRequest> diagnosisRequestOptional = Optional.of(diagnosisRequestWithId0);
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(diagnosisRequestOptional);
        doThrow(new NullPointerException()).when(diagnosisRequestRepository).deleteById(anyInt());
        Assertions.assertThrows(DeleteError.class, () -> diagnosisRequestService.deleteDiagnosisRequest(diagnosisRequestWithId0.getId()));
        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
        verify(diagnosisRequestRepository, times(1)).deleteById(anyInt());
    }


    @Test
    void shouldGetSingleDiagnosisRequest() {
        Optional<DiagnosisRequest> diagnosisRequestOptional = Optional.of(diagnosisRequestWithId0);
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(diagnosisRequestOptional);
        DiagnosisRequest testDiagnosisRequest = diagnosisRequestService.getDiagnosisRequest(diagnosisRequestWithId0.getId());
        Assertions.assertEquals(diagnosisRequestWithId0, testDiagnosisRequest);
        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingDiagnosisRequest() {
        when(diagnosisRequestRepository.findById(diagnosisRequestWithId0.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> diagnosisRequestService.getDiagnosisRequest(diagnosisRequestWithId0.getId()));
        verify(diagnosisRequestRepository, times(1)).findById(diagnosisRequestWithId0.getId());
    }

    @Test
    void shouldGetAllDiagnosisRequestsByChartId() {
        List<DiagnosisRequest> testDiagnosisRequestList = List.of(diagnosisRequestWithId0, diagnosisRequest3);
        when(diagnosisRequestRepository.findAllByIdChart(diagnosisRequestWithId0.getIdChart())).thenReturn(testDiagnosisRequestList);

        List<DiagnosisRequest> diagnosisRequests = diagnosisRequestService.getAllDiagnosisRequestsByChart(1);

        Assertions.assertEquals(testDiagnosisRequestList, diagnosisRequests);
        verify(diagnosisRequestRepository, times(1)).findAllByIdChart(diagnosisRequestWithId0.getIdChart());
    }

    @Test
    void shouldThrowWhenNoDiagnosisRequestsWithChartId() {
        when(diagnosisRequestRepository.findAllByIdChart(diagnosisRequestWithId0.getIdChart())).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> diagnosisRequestService.getAllDiagnosisRequestsByChart(diagnosisRequestWithId0.getIdChart()));

        verify(diagnosisRequestRepository, times(1)).findAllByIdChart(anyInt());
    }

    @Test
    void shouldGetAllDiagnosisRequestByChartId() {
        when(diagnosisRequestRepository.findByIdChart(diagnosisRequestWithId0.getIdChart())).thenReturn(Optional.of(diagnosisRequestWithId0));

        DiagnosisRequest diagnosisRequest = diagnosisRequestService.getDiagnosisRequestByChart(1);

        Assertions.assertEquals(diagnosisRequestWithId0, diagnosisRequest);
        verify(diagnosisRequestRepository, times(1)).findByIdChart(diagnosisRequestWithId0.getIdChart());
    }

    @Test
    void shouldThrowWhenNoDiagnosisRequestWithChartId() {
        when(diagnosisRequestRepository.findByIdChart(diagnosisRequestWithId0.getIdChart())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> diagnosisRequestService.getDiagnosisRequestByChart(diagnosisRequestWithId0.getIdChart()));

        verify(diagnosisRequestRepository, times(1)).findByIdChart(anyInt());
    }


}
