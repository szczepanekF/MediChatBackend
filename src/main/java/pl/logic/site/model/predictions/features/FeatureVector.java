package pl.logic.site.model.predictions.features;

import pl.logic.site.model.exception.IllegalGender;
import pl.logic.site.model.exception.IllegalHeight;
import pl.logic.site.model.exception.IllegalWeight;

import java.util.*;

import static pl.logic.site.utils.features.FeatureConsts.MAX_HEIGHT;
import static pl.logic.site.utils.features.FeatureConsts.MAX_WEIGHT;
import static pl.logic.site.utils.features.GenderDict.genderDict;
import static pl.logic.site.utils.features.SymptomsDict.symptomsDict;

/**
 * A class that represents a feature vector.
 * The class automatically converts the input data to numeric value.
 *
 * @author Kacper
 */
public class FeatureVector implements Vector {
    private double height;
    private double weight;
    private double gender;
    private Date birth_date;
    private HashMap<String, Double> symptoms;

    /**
     * Constructs a new feature vector with the specified personal information, date, and symptom values.
     * With automatically convert the given parameters to numeric values
     *
     * @param height    the height of the patient, (in cm)
     * @param weight    the weight of the patient, (in kilograms)
     * @param gender    the gender of the patient, as a string
     * @param birth_date the date of birth of the patient
     * @param symptoms  a map of symptom names to values, where the keys and values are a string.
     *                  Key is a symptom name, value is a symptom value
     * @throws IllegalGender    if the specified gender is not valid
     * @throws IllegalHeight    if the specified height is not valid
     * @throws IllegalWeight    if the specified weight is not valid
     */
    public FeatureVector(double height, double weight, String gender, Date birth_date, HashMap<String, String> symptoms)
            throws IllegalGender, IllegalHeight, IllegalWeight {
        this.height = heightToDouble(height);
        this.weight = weightToDouble(weight);
        this.gender = genderToDouble(gender);
        this.birth_date = birth_date;
        this.symptoms = new HashMap<>();
        symptomsToDouble(symptoms);
    }

    /**
     * Returns the personal information features of the feature vector.
     *
     * @return an array of personal information features
     */
    @Override
    public Double[] getPersonalInfoFeatures() {
        List<Double> result = new ArrayList<>();
        result.add(this.height);
        result.add(this.weight);
        result.add(this.gender);

        return result.toArray(new Double[0]);
    }

    /**
     * Returns the date features of the feature vector.
     *
     * @return an array of date features
     */
    @Override
    public Date[] getDateFeatures() {
        List<Date> result = new ArrayList<>();
        result.add(this.birth_date);

        return result.toArray(new Date[0]);
    }

    /**
     * Returns the symptom features of the feature vector.
     *
     * @return an array of symptom features
     */
    @Override
    public Double[] getSymptomFeatures() {
        List<Double> result = new ArrayList<>(this.symptoms.values());

        return result.toArray(new Double[0]);
    }

    /**
     * Converts a gender string to a numeric representation.
     *
     * @param gender the gender as a string
     * @return the numeric representation of the gender
     * @throws IllegalGender if the specified gender is not valid (is not in GenderDict)
     */
    private double genderToDouble(String gender) {
        String tempGender = gender.toLowerCase();
        for (int i = 0; i < genderDict.size(); i++) {
            if (tempGender.equals(genderDict.keySet().toArray()[i])) {
                return genderDict.get(tempGender);
            }
        }
        throw new IllegalGender("Invalid gender dictionary");
    }

    /**
     * Converts a height to a normalized value between 0 and 1.
     *
     * @param height the height of patient, (in cm)
     * @return the normalized height value
     * @throws IllegalHeight if the specified height is not valid
     */
    private double heightToDouble(Double height) {
        if (height == null || height <= 0.) {
            throw new IllegalHeight("Invalid height");

        }
        if (height > MAX_HEIGHT) {
            throw new IllegalHeight("Height is too high");
        }
        return height / MAX_HEIGHT;
    }

    /**
     * Converts a weight to a normalized value between 0 and 1.
     *
     * @param weight the weight of patient, (in kg)
     * @return the normalized weight value
     * @throws IllegalWeight if the specified weight is not valid
     */
    private double weightToDouble(Double weight) {
        if (weight == null || weight <= 0.) {
            throw new IllegalWeight("Invalid weight");
        }
        if (weight > MAX_WEIGHT) {
            throw new IllegalWeight("Weight is too high");
        }
        return weight / MAX_WEIGHT;
    }

    /**
     * Converts a map of symptoms' values which are Strings to normalized numeric values.
     *
     * @param symptoms a map of symptom names and (String) values
     */
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
