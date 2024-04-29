package pl.logic.site.utils.predictions;

import java.util.HashMap;

/**
 * A static class that contains a mapping of gender strings to double values.
 * The class for converting String gender into a numeric value.
 *
 * @author Kacper
 */
public class GenderDict {
    /**
     * A mapping of gender strings to double values, where 0. represents male and 1. represents female.
     */
    public static final HashMap<String, Double> genderDict = new HashMap<String, Double>() {{
        put("male", 0.);
        put("female", 1.);
    }};
}
