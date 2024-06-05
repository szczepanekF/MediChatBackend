package pl.logic.site.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import pl.logic.site.aspects.AuthorizationHeaderHolder;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthorizationHeaderAspect {

    @Autowired
    private HttpServletRequest request;

    @Pointcut("execution(public * pl.logic.site.controller..*(..))")
    public void controllerMethods() {}

    @Pointcut("@annotation(org.springframework.messaging.handler.annotation.MessageMapping)")
    public void messageMappingMethods() {}

    @Before("controllerMethods() && !messageMappingMethods()")
    public void beforePost() {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null) {
            AuthorizationHeaderHolder.setAuthorizationHeader(authorizationHeader.substring(7));
        } else {
            System.out.println("Authorization header is missing");
        }
    }
}
