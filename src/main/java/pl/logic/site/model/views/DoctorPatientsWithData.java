package pl.logic.site.model.views;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.logic.site.model.mysql.Patient;

import java.util.Date;

@Data
@AllArgsConstructor
public class DoctorPatientsWithData {

    Patient patient;
    private String isDiagnosisRequest;
    private Date dateofcontact;
}
