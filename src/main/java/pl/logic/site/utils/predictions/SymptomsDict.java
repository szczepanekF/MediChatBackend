package pl.logic.site.utils.predictions;

import java.util.HashMap;

/**
 * This class contains a list of symptoms (should be all symptoms in database) and their respective weights.
 * The weights are used to calculate the severity score of a symptom.
 * A dictionary class for changing the symptom value from String to a numeric value.
 *
 * @author Kacper
 */
public class SymptomsDict {
    /**
     * A map containing the symptoms and their weights.
     */
    public static final HashMap<String, Double> symptomsDict = new HashMap<String, Double>() {{
        put("null", 0.0);
        put("mild", 0.2);
        put("strong", 0.8);
        put("moderate", 0.5);
        put("severe", 0.95);
        put("sharp", 0.9);
        put("frequent", 0.85);
        put("rare", 0.3);
        put("high", 0.6);
        put("very high", 0.75);
        put("extremely high", 1.);
        put("low", 0.6);
        put("very low", 0.75);
        put("extremely low", 1.);
        put("persistent", 0.65);
        put("clear", 0.1);
        put("sudden", 0.5);
        put("blurred", 0.35);
        put("restless", 0.4);
        put("intermittent", 0.4);
        put("vertigo", 0.6);
        put("itchy", 0.4);
        put("profuse", 0.9);
        put("cramping", 0.75);
        put("prolonged", 0.65);
        put("bloating", 0.15);
        put("chronic", 0.7);
    }};
}
