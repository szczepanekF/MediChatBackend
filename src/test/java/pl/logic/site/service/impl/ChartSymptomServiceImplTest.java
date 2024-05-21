package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.dao.ChartSymptomDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.ChartSymptom;
import pl.logic.site.repository.ChartSymptomRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ChartSymptomServiceImplTest {
    @Mock
    private ChartSymptomRepository chartSymptomRepository;
    @InjectMocks
    private ChartSymptomServiceImpl chartSymptomService;
    ChartSymptom chartSymptom1;
    ChartSymptom chartSymptom2;
    ChartSymptom chartSymptom3;

    @BeforeEach
    public void setUp() {
        chartSymptom1 = new ChartSymptom(1, 30, 2, "");
        chartSymptom2 = new ChartSymptom(2, 30, 5, "");
        chartSymptom3 = new ChartSymptom(3, 31, 2, "");
    }

    @Test
    void shouldCreateChartSymptom() {
        when(chartSymptomRepository.saveAndFlush(any(ChartSymptom.class))).thenReturn(chartSymptom1);

        ChartSymptomDAO chartSymptomDAO = new ChartSymptomDAO(chartSymptom1);
        ChartSymptom savedChartSymptom = chartSymptomService.createChartSymptom(chartSymptomDAO);

        verify(chartSymptomRepository, times(1)).saveAndFlush(any(ChartSymptom.class));
        Assertions.assertEquals(chartSymptom1, savedChartSymptom);
    }

//    @Test
//    void shouldThrowWhileCreatingChartSymptomWithId() {
//
//        chartSymptom2.setId(0);
//        ChartSymptomDAO chartSymptomDAO = new ChartSymptomDAO(chartSymptom2);
//        Assertions.assertThrows(SaveError.class, () -> chartSymptomService.createChartSymptom(chartSymptomDAO));
//        verify(chartSymptomRepository, never()).saveAndFlush(any(ChartSymptom.class));
//    }

    @Test
    void shouldThrowWhenRepositoryErrorCreatingChartSymptom() {
        doThrow(new NullPointerException()).when(chartSymptomRepository).saveAndFlush(any(ChartSymptom.class));
        ChartSymptomDAO chartSymptomDAO = new ChartSymptomDAO(chartSymptom1);
        Assertions.assertThrows(SaveError.class, () -> chartSymptomService.createChartSymptom(chartSymptomDAO));
        verify(chartSymptomRepository, times(1)).saveAndFlush(any(ChartSymptom.class));

    }

    @Test
    void shouldUpdateChartSymptom() {
        Optional<ChartSymptom> chartSymptomOptional = Optional.of(chartSymptom1);
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(chartSymptomOptional);
        when(chartSymptomRepository.saveAndFlush(any(ChartSymptom.class))).thenReturn(chartSymptom1);

        ChartSymptomDAO chartSymptomDAO = new ChartSymptomDAO(chartSymptom1);
        ChartSymptom savedChartSymptom = chartSymptomService.updateChartSymptom(chartSymptomDAO, chartSymptom1.getId());

        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
        verify(chartSymptomRepository, times(1)).saveAndFlush(any(ChartSymptom.class));
        Assertions.assertEquals(chartSymptom1, savedChartSymptom);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingChartSymptom() {
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(Optional.empty());


        ChartSymptomDAO chartSymptomDAO = new ChartSymptomDAO(chartSymptom1);
        Assertions.assertThrows(EntityNotFound.class, () -> chartSymptomService.updateChartSymptom(chartSymptomDAO, chartSymptom1.getId()));
        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
        verify(chartSymptomRepository, never()).saveAndFlush(any(ChartSymptom.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingChartSymptom() {
        Optional<ChartSymptom> chartSymptomOptional = Optional.of(chartSymptom1);
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(chartSymptomOptional);
        doThrow(new NullPointerException()).when(chartSymptomRepository).saveAndFlush(any(ChartSymptom.class));
        ChartSymptomDAO chartSymptomDAO = new ChartSymptomDAO(chartSymptom1);
        Assertions.assertThrows(SaveError.class, () -> chartSymptomService.updateChartSymptom(chartSymptomDAO, chartSymptom1.getId()));
        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
        verify(chartSymptomRepository, times(1)).saveAndFlush(any(ChartSymptom.class));
    }

    @Test
    void shouldDeleteChartSymptom() {
        Optional<ChartSymptom> chartSymptomOptional = Optional.of(chartSymptom1);
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(chartSymptomOptional);

        chartSymptomService.deleteChartSymptom(chartSymptom1.getId());

        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
        verify(chartSymptomRepository, times(1)).deleteById(chartSymptom1.getId());
    }

    @Test
    void shouldThrowWhileDeletingNonExistingChartSymptom() {
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFound.class, () -> chartSymptomService.deleteChartSymptom(chartSymptom1.getId()));
        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
        verify(chartSymptomRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenRepositoryErrorDeletingChartSymptom() {
        Optional<ChartSymptom> chartSymptomOptional = Optional.of(chartSymptom1);
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(chartSymptomOptional);
        doThrow(new NullPointerException()).when(chartSymptomRepository).deleteById(anyInt());
        Assertions.assertThrows(DeleteError.class, () -> chartSymptomService.deleteChartSymptom(chartSymptom1.getId()));
        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
        verify(chartSymptomRepository, times(1)).deleteById(anyInt());
    }


    @Test
    void shouldGetSingleChartSymptom() {
        Optional<ChartSymptom> chartSymptomOptional = Optional.of(chartSymptom1);
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(chartSymptomOptional);
        ChartSymptom testChartSymptom = chartSymptomService.getChartSymptom(chartSymptom1.getId());
        Assertions.assertEquals(chartSymptom1, testChartSymptom);
        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingChartSymptom() {
        when(chartSymptomRepository.findById(chartSymptom1.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> chartSymptomService.getChartSymptom(chartSymptom1.getId()));
        verify(chartSymptomRepository, times(1)).findById(chartSymptom1.getId());
    }

    @Test
    void shouldGetAllChartSymptoms() {
        when(chartSymptomRepository.findAllByIdChart(chartSymptom1.getId())).thenReturn(List.of(chartSymptom1, chartSymptom2));

        List<ChartSymptom> chartSymptoms = chartSymptomService.getChartSymptoms(chartSymptom1.getId());

        Assertions.assertEquals(List.of(chartSymptom1, chartSymptom2), chartSymptoms);
        verify(chartSymptomRepository, times(1)).findAllByIdChart(chartSymptom1.getId());
    }

    @Test
    void shouldThrowWhenNoChartSymptomsInRepository() {
        when(chartSymptomRepository.findAllByIdChart(chartSymptom1.getId())).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> chartSymptomService.getChartSymptoms(chartSymptom1.getId()));

        verify(chartSymptomRepository, times(1)).findAllByIdChart(chartSymptom1.getId());
    }

}
