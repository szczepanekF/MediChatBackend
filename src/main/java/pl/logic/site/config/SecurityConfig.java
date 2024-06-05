package pl.logic.site.config;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.exception.UserNotFound;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Role;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.model.request.LoginRequest;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.repository.SpringUserRepository;
import pl.logic.site.service.impl.AuthenticationServiceImpl;
import pl.logic.site.service.impl.JwtServiceImpl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final Filter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private static final String[] WHITE_LIST_URL = {"**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.oidcUserService(this.oidcUserService())
                        )
                        .successHandler(customAuthenticationSuccessHandler)
                )

//                .formLogin(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return new OidcUserService();
    }
}

@Component
@RequiredArgsConstructor
class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
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

