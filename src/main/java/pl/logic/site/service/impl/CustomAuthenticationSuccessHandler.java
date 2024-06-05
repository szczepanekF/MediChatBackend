package pl.logic.site.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.exception.UserNotFound;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Role;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.repository.SpringUserRepository;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthenticationServiceImpl authenticationService;
    private final SpringUserRepository springUserRepository;
    private final PatientRepository patientRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        String firstName = oidcUser.getGivenName();
        String lastName = oidcUser.getFamilyName();

        SpringUser springUser;
        if(springUserRepository.findByEmail(email).isEmpty()) {
            Patient patient = Patient.builder()
                    .name(firstName)
                    .surname(lastName)
                    .birth_date(new Date())
                    .height(180)
                    .weight(80)
                    .gender("male")
                    .status(Status.ONLINE)
                    .heightUnit("centimeter")
                    .weightUnit("kilograms")
                    .build();
            patientRepository.save(patient);
            springUser = SpringUser.builder()
                    .email(email)
                    .username(email.split("@")[0])
                    .password("")
                    .doctorId(null)
                    .patientId(patient.getId())
                    .role(Role.PATIENT)
                    .creationDate(new Date())
                    .build();
            springUserRepository.save(springUser);
        } else {
            springUser = springUserRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new UserNotFound("User with this username or email address does not exist"));
        }
        String token = authenticationService.loginWithGoogle(springUser);
        String targetUrl = String.format("http://localhost:3000/login?token=%s", token);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}


