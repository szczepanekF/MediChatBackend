package pl.logic.site.service;

import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.mysql.Specialisation;

import java.util.List;

public interface SpecialisationService {
    Specialisation createSpecialisation(SpecialisationDAO specialisation);

    void deleteSpecialisation(int id);

    Specialisation updateSpecialisation(SpecialisationDAO specialisation, int id);

    Specialisation getSpecialisation(int id);

    List<Specialisation> getSpecialisations();

}
