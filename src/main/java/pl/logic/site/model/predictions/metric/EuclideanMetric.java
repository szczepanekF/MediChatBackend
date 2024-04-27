package pl.logic.site.model.predictions.metric;

import pl.logic.site.model.predictions.features.IFeatureVector;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static pl.logic.site.utils.features.FeatureConsts.MAX_DATE_DIFF;

public class EuclideanMetric implements Metric {

    @Override
    public double calculateMetric(IFeatureVector v1, IFeatureVector v2) {
        Double[] v1PersonalInfo = v1.getPersonalInfoFeatures();
        Date[] v1Date = v1.getDateFeatures();
        Double[] v1Symptoms = v1.getSymptomFeatures();

        Double[] v2PersonalInfo = v2.getPersonalInfoFeatures();
        Date[] v2Date = v2.getDateFeatures();
        Double[] v2Symptoms = v2.getSymptomFeatures();

        Double personalInfo = calculateNumeric(v1PersonalInfo, v2PersonalInfo);
        Double date = calculateDate(v1Date, v2Date);
        Double symptoms = calculateNumeric(v1Symptoms, v2Symptoms);

        return Math.sqrt(personalInfo + date + symptoms);
    }

    private double calculateNumeric(Double[] v1, Double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Arrays v1 and v2 must have the same length");
        }
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += Math.pow((v1[i] - v2[i]), 2);
        }
        return result;
    }

    private double calculateDate(Date[] v1Date, Date[] v2Date) {
        double result = 0;
        long diffInMillies;
        long diff;
        double normalization;
        for (int i = 0; i < v1Date.length; i++) {
            diffInMillies = Math.abs(v2Date[i].getTime() - v1Date[i].getTime());
            diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            normalization = (double) diff / MAX_DATE_DIFF;
            result += Math.pow(normalization, 2);
        }
        return result;
    }
}
