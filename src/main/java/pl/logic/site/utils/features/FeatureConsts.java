package pl.logic.site.utils.features;

import java.util.HashMap;

/**
 * This class contains all the constants used in the features package.
 * Most of them are used to normalize feature values or calculations
 *
 *  @author Kacper
 */
public class FeatureConsts {
    /**
     * The maximum height allowed for a person (cm).
     */
    public static final double MAX_HEIGHT = 250.;
    /**
     * The maximum weight allowed for a person (kg).
     */
    public static final double MAX_WEIGHT = 150.;
    /**
     * The difference in age between two people.
     * Assuming the maximum difference is 100 years times the number of days in a year but
     * divided by 2 to increase the contribution of the date to the calculations.
     * This is 50 years in days
     * The value range is <0, 2>.
     */
    public static final long MAX_DATE_DIFF = 18250;

}
