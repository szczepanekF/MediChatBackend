package pl.logic.site.model.predictions.metric;

import pl.logic.site.model.predictions.features.Vector;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static pl.logic.site.utils.predictions.PredictionConsts.MAX_DATE_DIFF;

/**
 * Calculates the Euclidean distance (as a metric) between two vectors.
 *
 * @author Kacper
 */
public class EuclideanMetric implements Metric {

    /**
     * Calculates the entire Euclidean distance between two vectors.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the Euclidean distance between the two vectors
     */
    @Override
    public double calculateMetric(Vector v1, Vector v2) {
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

    /**
     * Calculates the sum of the squares of the differences between the values of two lists.
     *
     * @param v1 the first array of numbers
     * @param v2 the second array of numbers
     * @return the squared Euclidean distance between the two arrays
     */
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

    /**
     * Calculates the sum of the squares of the differences between the values of two Dates' lists.
     *
     * @param v1Date the first array of dates
     * @param v2Date the second array of dates
     * @return the squared Euclidean distance between the two Dates arrays
     */
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
