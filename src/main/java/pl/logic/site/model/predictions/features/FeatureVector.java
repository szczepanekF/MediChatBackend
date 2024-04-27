package pl.logic.site.model.predictions.features;

import pl.logic.site.model.exception.IllegalGender;
import pl.logic.site.model.exception.IllegalHeight;
import pl.logic.site.model.exception.IllegalWeight;

import java.util.*;

import static pl.logic.site.utils.features.FeatureConsts.MAX_HEIGHT;
import static pl.logic.site.utils.features.FeatureConsts.MAX_WEIGHT;
import static pl.logic.site.utils.features.GenderDict.genderDict;
import static pl.logic.site.utils.features.SymptomsDict.symptomsDict;

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
        symptomsToDouble(symptoms);
    }

    @Override
    public Double[] getPersonalInfoFeatures() {
        List<Double> result = new ArrayList<>();
        result.add(this.height);
        result.add(this.weight);
        result.add(this.gender);

        return result.toArray(new Double[0]);
    }

    @Override
    public Date[] getDateFeatures() {
        List<Date> result = new ArrayList<>();
        result.add(this.birth_date);

        return result.toArray(new Date[0]);
    }

    @Override
    public Double[] getSymptomFeatures() {
        List<Double> result = new ArrayList<>(this.symptoms.values());

        return result.toArray(new Double[0]);
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
        for (Map.Entry<String, String> entry : symptoms.entrySet()) {
            String symptom = entry.getKey();
            String value = entry.getValue();
            for (int i = 0; i < symptomsDict.size(); i++) {
                if (value.equals(symptomsDict.keySet().toArray()[i])) {
                    this.symptoms.put(symptom, symptomsDict.get(value));
                }
            }
        }
    }
}
