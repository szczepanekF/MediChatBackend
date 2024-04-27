package pl.logic.site.utils.features;

import java.util.HashMap;

public class FeatureConsts {
    public static final double MAX_HEIGHT = 250.;
    public static final double MAX_WEIGHT = 150.;
    // assuming the maximum difference is 100 years
    // times the number of days in a year but
    // divided by 2 to increase the contribution
    // of the date to the calculations.
    // The value range is <0, 2>
    // 50 years in days
    public static final long MAX_DATE_DIFF = 18250;

}
