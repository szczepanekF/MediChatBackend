package pl.logic.site.aspects;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationHeaderAspect {

    @Autowired
    private HttpServletRequest request;

    @Before("execution(public * pl.logic.site.controller..*(..))")
    public void beforePost() {
        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader != null) {
            AuthorizationHeaderHolder.setAuthorizationHeader(authorizationHeader.substring(7));
        } else {
            System.out.println("Authorization header is missing");
        }
    }
}