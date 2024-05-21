package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.repository.ChartRepository;
import pl.logic.site.repository.DiagnosisRequestRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ChartServiceImplTest {
    @Mock
    private DiagnosisRequestRepository diagnosisRequestRepository;
    @Mock
    private ChartRepository chartRepository;
    @InjectMocks
    private ChartServiceImpl chartService;
    Chart chartWithId0;
    Chart chart2;
    Chart chart3;


    @BeforeEach
    public void setUp() {
        chartWithId0 = new Chart(0, 2, new Date());
        chart2 = new Chart(1, 1, new Date());
        chart3 = new Chart(2, 2, new Date());
    }

    @Test
    void shouldCreateChart() {
        when(chartRepository.saveAndFlush(any(Chart.class))).thenReturn(chartWithId0);

        ChartDAO chartDAO = new ChartDAO(chartWithId0);
        Chart savedChart = chartService.createChart(chartDAO);

        verify(chartRepository, times(1)).saveAndFlush(any(Chart.class));
        Assertions.assertEquals(chartWithId0, savedChart);
    }

    @Test
    void shouldThrowWhileCreatingChartWithId() {


        ChartDAO chartDAO = new ChartDAO(chart2);
        Assertions.assertThrows(SaveError.class, () -> chartService.createChart(chartDAO));
        verify(chartRepository, never()).saveAndFlush(any(Chart.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorCreatingChart() {
        doThrow(new NullPointerException()).when(chartRepository).saveAndFlush(any(Chart.class));
        ChartDAO chartDAO = new ChartDAO(chartWithId0);
        Assertions.assertThrows(SaveError.class, () -> chartService.createChart(chartDAO));
        verify(chartRepository, times(1)).saveAndFlush(any(Chart.class));

    }

    @Test
    void shouldUpdateChart() {
        Optional<Chart> chartOptional = Optional.of(chartWithId0);
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(chartOptional);
        when(chartRepository.saveAndFlush(any(Chart.class))).thenReturn(chartWithId0);

        ChartDAO chartDAO = new ChartDAO(chartWithId0);
        Chart savedChart = chartService.updateChart(chartDAO, chartWithId0.getId());

        verify(chartRepository, times(1)).findById(chartWithId0.getId());
        verify(chartRepository, times(1)).saveAndFlush(any(Chart.class));
        Assertions.assertEquals(chartWithId0, savedChart);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingChart() {
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(Optional.empty());


        ChartDAO chartDAO = new ChartDAO(chartWithId0);
        Assertions.assertThrows(EntityNotFound.class, () -> chartService.updateChart(chartDAO, chartWithId0.getId()));
        verify(chartRepository, times(1)).findById(chartWithId0.getId());
        verify(chartRepository, never()).saveAndFlush(any(Chart.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingChart() {
        Optional<Chart> chartOptional = Optional.of(chartWithId0);
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(chartOptional);
        doThrow(new NullPointerException()).when(chartRepository).saveAndFlush(any(Chart.class));
        ChartDAO chartDAO = new ChartDAO(chartWithId0);
        Assertions.assertThrows(SaveError.class, () -> chartService.updateChart(chartDAO, chartWithId0.getId()));
        verify(chartRepository, times(1)).findById(chartWithId0.getId());
        verify(chartRepository, times(1)).saveAndFlush(any(Chart.class));
    }

    @Test
    void shouldDeleteChart() {
        Optional<Chart> chartOptional = Optional.of(chartWithId0);
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(chartOptional);

        chartService.deleteChart(chartWithId0.getId());

        verify(chartRepository, times(1)).findById(chartWithId0.getId());
        verify(chartRepository, times(1)).deleteById(chartWithId0.getId());
    }

    @Test
    void shouldThrowWhileDeletingNonExistingChart() {
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFound.class, () -> chartService.deleteChart(chartWithId0.getId()));
        verify(chartRepository, times(1)).findById(chartWithId0.getId());
        verify(chartRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenRepositoryErrorDeletingChart() {
        Optional<Chart> chartOptional = Optional.of(chartWithId0);
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(chartOptional);
        doThrow(new NullPointerException()).when(chartRepository).deleteById(anyInt());
        Assertions.assertThrows(DeleteError.class, () -> chartService.deleteChart(chartWithId0.getId()));
        verify(chartRepository, times(1)).findById(chartWithId0.getId());
        verify(chartRepository, times(1)).deleteById(anyInt());
    }


    @Test
    void shouldGetSingleChart() {
        Optional<Chart> chartOptional = Optional.of(chartWithId0);
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(chartOptional);
        Chart testChart = chartService.getChart(chartWithId0.getId());
        Assertions.assertEquals(chartWithId0, testChart);
        verify(chartRepository, times(1)).findById(chartWithId0.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingChart() {
        when(chartRepository.findById(chartWithId0.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> chartService.getChart(chartWithId0.getId()));
        verify(chartRepository, times(1)).findById(chartWithId0.getId());
    }
    @Test
    void shouldGetAllChartsForPatient() {

        List<Chart> testChartList = List.of(chartWithId0, chart3);
        when(chartRepository.findByIdPatient(chartWithId0.getIdPatient())).thenReturn(testChartList);

        List<Chart> charts = chartService.getChartsForPatient(chartWithId0.getIdPatient());

        Assertions.assertEquals(testChartList, charts);
        verify(chartRepository, times(1)).findByIdPatient(chartWithId0.getIdPatient());
    }

    @Test
    void shouldThrowWhenNoChartsInRepositoryForSpecifiedPatient() {
        when(chartRepository.findByIdPatient(chartWithId0.getIdPatient())).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> chartService.getChartsForPatient(chartWithId0.getIdPatient()));

        verify(chartRepository, times(1)).findByIdPatient(chartWithId0.getIdPatient());
    }

    static Stream<Arguments> provideStateAndDiagnosis() {
        return Stream.of(
                Arguments.of(1, List.of("test1", "", "test3", "")),
                Arguments.of(0, List.of("test4", "", "", "test5"))
        );
    }

    @ParameterizedTest()
    @MethodSource("provideStateAndDiagnosis")
    void shouldGetAllDiagnosisRequestChartsForPatientParametrized(int state, List<String> diagnosis) {

        List<Chart> testChartList = List.of(chartWithId0, chart2, chart3);
        when(chartRepository.findByIdPatient(chartWithId0.getIdPatient())).thenReturn(testChartList);
        DiagnosisRequest diagnosisRequest1 = DiagnosisRequest.builder().id(1).diagnosis(diagnosis.get(0)).build();
        DiagnosisRequest diagnosisRequest2 = DiagnosisRequest.builder().id(2).diagnosis(diagnosis.get(1)).build();
        DiagnosisRequest diagnosisRequest3 = DiagnosisRequest.builder().id(3).diagnosis(diagnosis.get(2)).build();
        DiagnosisRequest diagnosisRequest4 = DiagnosisRequest.builder().id(4).diagnosis(diagnosis.get(3)).build();
        when(diagnosisRequestRepository.findAllByIdChart(chartWithId0.getId())).thenReturn(List.of(diagnosisRequest1, diagnosisRequest2));
        when(diagnosisRequestRepository.findAllByIdChart(chart2.getId())).thenReturn(List.of(diagnosisRequest3));
        when(diagnosisRequestRepository.findAllByIdChart(chart3.getId())).thenReturn(List.of(diagnosisRequest4));

        List<Chart> charts = chartService.getChartsByStateAndPatientId(state, chartWithId0.getIdPatient());

        Assertions.assertEquals(List.of(chartWithId0, chart2), charts);
        verify(chartRepository, times(1)).findByIdPatient(chartWithId0.getIdPatient());
        verify(diagnosisRequestRepository, times(1)).findAllByIdChart(chartWithId0.getId());
        verify(diagnosisRequestRepository, times(1)).findAllByIdChart(chart2.getId());
    }

    @Test
    void shouldReturnEmptyListWhenThereIsNoEmptyDiagnosisRequestChartForPatientIdInRepository() {
        int exisitngDiagnosticRequestState = 1;
        List<Chart> testChartList = List.of(chartWithId0, chart2, chart3);
        when(chartRepository.findByIdPatient(chartWithId0.getIdPatient())).thenReturn(testChartList);
        when(diagnosisRequestRepository.findAllByIdChart(anyInt())).thenReturn(List.of());

        List<Chart> charts = chartService.getChartsByStateAndPatientId(exisitngDiagnosticRequestState, chartWithId0.getIdPatient());

        Assertions.assertTrue(charts.isEmpty());
        verify(chartRepository, times(1)).findByIdPatient(chartWithId0.getIdPatient());
        verify(diagnosisRequestRepository, times(3)).findAllByIdChart(anyInt());

    }

    @Test
    void shouldReturnChartsWhenThereIsNoDiagnosisRequestChartForPatientIdInRepository() {
        int exisitngDiagnosticRequestState = 0;
        List<Chart> testChartList = List.of(chartWithId0, chart2, chart3);
        when(chartRepository.findByIdPatient(chartWithId0.getIdPatient())).thenReturn(testChartList);
        when(diagnosisRequestRepository.findAllByIdChart(anyInt())).thenReturn(List.of());

        List<Chart> charts = chartService.getChartsByStateAndPatientId(exisitngDiagnosticRequestState, chartWithId0.getIdPatient());
        Assertions.assertEquals(testChartList, charts);

        verify(chartRepository, times(1)).findByIdPatient(chartWithId0.getIdPatient());
        verify(diagnosisRequestRepository, times(3)).findAllByIdChart(anyInt());

    }

}
