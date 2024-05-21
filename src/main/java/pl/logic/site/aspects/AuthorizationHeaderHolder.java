package pl.logic.site.aspects;

public class AuthorizationHeaderHolder {
    private static final ThreadLocal<String> authorizationHeader = new ThreadLocal<>();

    public static void setAuthorizationHeader(String header) {
        authorizationHeader.set(header);
    }

    public static String getAuthorizationHeader() {
        return authorizationHeader.get();
    }

    public static void clear() {
        authorizationHeader.remove();
    }
}
