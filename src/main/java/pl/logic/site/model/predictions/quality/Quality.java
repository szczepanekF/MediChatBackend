package pl.logic.site.model.predictions.quality;

import java.util.List;

public class Quality {
    public static Double calculateAccuracy(List<Result> diseases){
        Double accumulator = 0.0;
        for(Label disease :diseases){
            if(disease.getExpected().getName().equals(disease.getResult().getName())) accumulator++;
        }
        return (accumulator/diseases.size());
    }
}
