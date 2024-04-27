package pl.logic.site.model.predictions.quality;

import pl.logic.site.model.mysql.Disease;

public interface Label {
    public Disease getExpected();
    public Disease getResult();
}
