package pl.logic.site.utils.features;

import java.util.HashMap;

public class GenderDict {
    public static final HashMap<String, Double> genderDict = new HashMap<String, Double>() {{
        put("male", 0.);
        put("female", 1.);
    }};
}
