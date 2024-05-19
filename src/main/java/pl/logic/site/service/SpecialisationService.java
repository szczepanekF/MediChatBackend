package pl.logic.site.service;

import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.mysql.Specialisation;

import java.util.List;

public interface SpecialisationService {
    Specialisation createSpecialisation(SpecialisationDAO specialisation);

    void deleteSpecialisation(int specialisationId);

    Specialisation updateSpecialisation(SpecialisationDAO specialisation, int specialisationId);

    Specialisation getSpecialisation(int specialisationId);

    List<Specialisation> getSpecialisations();

}
