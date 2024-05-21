package pl.logic.site.aspects;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import java.util.Map;

public class ControllerUtils {

    public static String getControllerName(HttpServletRequest request) {
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null) {
            return handlerMethod.getBeanType().getSimpleName();
        }
        return null;
    }

    public static String getMethodName(HttpServletRequest request) {
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null) {
            return handlerMethod.getMethod().getName();
        }
        return null;
    }

    public static String getEndpointPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return requestUri.substring(contextPath.length());
    }

    public static String combinePaths(HttpServletRequest request) {
        return getControllerName(request)+" -> "+getEndpointPath(request)+" -> "+getMethodName(request)+" : ";
    }
}