package pl.logic.site.model.predictions.quality;

import pl.logic.site.model.mysql.Disease;

/**
 * An interface for a label that is used to wrap the expected disease prediction result.
 *
 * @author Kacper
 */
public interface Label {

    /**
     * Returns the expected disease for a given patient.
     * Important: If patient has not had diagnosed disease (e.g. diagnosis from Doctor) the value can be null!
     *
     * @return the expected disease
     */
    public Disease getExpected();

    /**
     * Returns the actual disease that was predicted for a given patient.
     *
     * @return the actual disease
     */
    public Disease getResult();

}
