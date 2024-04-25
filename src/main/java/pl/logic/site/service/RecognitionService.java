package pl.logic.site.service;

import pl.logic.site.model.dao.RecognitionDAO;
import pl.logic.site.model.mysql.Recognition;

import java.util.List;

public interface RecognitionService {
    Recognition createRecognition(RecognitionDAO recognition);

    void deleteRecognition(int id);

    Recognition updateRecognition(RecognitionDAO recognition, int id);

    Recognition getRecognition(int id);

    List<Recognition> getRecognitions();

}
