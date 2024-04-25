package pl.logic.site.model.predictions;

import pl.logic.site.model.exception.IllegalGender;
import pl.logic.site.model.exception.IllegalHeight;
import pl.logic.site.model.exception.IllegalWeight;

import java.util.Date;
import java.util.HashMap;

import static pl.logic.site.utils.features.FeatureConsts.MAX_HEIGHT;
import static pl.logic.site.utils.features.FeatureConsts.MAX_WEIGHT;
import static pl.logic.site.utils.features.GenderDict.genderDict;

public class FeatureVector implements IFeatureVector {
    private double height;
    private double weight;
    private double gender;
    private Date birth_date;
    private HashMap<String, Double> symptoms;

    public FeatureVector(double height, double weight, String gender, Date birth_date, HashMap<String, String> symptoms)
            throws IllegalGender, IllegalHeight, IllegalWeight {
        this.height = heightToDouble(height);
        this.weight = weightToDouble(weight);
        this.gender = genderToDouble(gender);
        this.birth_date = birth_date;
        this.symptoms = new HashMap<>();
    }

    @Override
    public Double[] getPersonalInfoFeatures() {
        return new Double[0];
    }

    @Override
    public Date[] getDateFeatures() {
        return new Date[0];
    }

    @Override
    public Double[] getSymptomFeatures() {
        return new Double[0];
    }

    private double genderToDouble(String gender) {
        String tempGender = gender.toLowerCase();
        for (int i = 0; i < genderDict.size(); i++) {
            if (tempGender.equals(genderDict.keySet().toArray()[i])) {
                return genderDict.get(tempGender);
            }
        }
        throw new IllegalGender("Invalid gender dictionary");
    }

    private double heightToDouble(Double height) {
        if (height == null || height <= 0.) {
            throw new IllegalHeight("Invalid height");

        }
        if (height > MAX_HEIGHT) {
            throw new IllegalHeight("Height is too high");
        }
        return height / MAX_HEIGHT;
    }

    private double weightToDouble(Double weight) {
        if (weight == null || weight <= 0.) {
            throw new IllegalWeight("Invalid weight");
        }
        if (weight > MAX_WEIGHT) {
            throw new IllegalWeight("Weight is too high");
        }
        return weight / MAX_WEIGHT;
    }

    private void symptomsToDouble(HashMap<String, String> symptoms) {
//        for (String symptom : symptoms.keySet()) {
//            this.symptoms.put(symptom, Double.parseDouble(symptoms.get(symptom)));
//        }
        //TODO finish symptoms
    }
}
