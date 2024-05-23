
package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.exception.EntityNotFound;

import pl.logic.site.model.mysql.Disease;

import pl.logic.site.repository.DiseaseRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DiseaseServiceImplTest {

    @Mock
    private DiseaseRepository diseaseRepository;
    @InjectMocks
    private DiseaseServiceImpl diseaseService;
    Disease disease1;
    Disease disease2;

    @BeforeEach
    public void setUp() {
        disease1 = new Disease(1, "pneumonia");
        disease2 = new Disease(2, "motion sickness");
    }
           

    @Test
    void shouldGetSingleDisease() {
        Optional<Disease> diseaseOptional = Optional.of(disease1);
        when(diseaseRepository.findById(disease1.getId())).thenReturn(diseaseOptional);
        Disease testDisease = diseaseService.getDisease(disease1.getId());
        Assertions.assertEquals(disease1, testDisease);
        verify(diseaseRepository, times(1)).findById(disease1.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingDisease() {
        when(diseaseRepository.findById(disease1.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> diseaseService.getDisease(disease1.getId()));
        verify(diseaseRepository, times(1)).findById(disease1.getId());
    }

    @Test
    void shouldGetAllDiseases() {
        List<Disease> testDiseaseList = List.of(disease1, disease2);
        when(diseaseRepository.findAll()).thenReturn(testDiseaseList);

        List<Disease> diseases = diseaseService.getDiseases();

        Assertions.assertEquals(testDiseaseList, diseases);
        verify(diseaseRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoDiseasesInRepository() {
        when(diseaseRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> diseaseService.getDiseases());

        verify(diseaseRepository, times(1)).findAll();
    }

}
