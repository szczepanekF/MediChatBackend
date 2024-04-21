package pl.logic.site.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.logic.site.model.enums.Status;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPatientRequest {
    private String name;
    private String surname;
    private Date birthDate;
    private int height;
    private int weight;
    private String gender;
    private Status status;
    private String email;
    private String username;
    private String password;
    private String heightUnit;
    private String weightUnit;
}
