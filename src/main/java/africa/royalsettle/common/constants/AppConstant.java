package africa.royalsettle.common.constants;

import java.time.format.DateTimeFormatter;

public class AppConstant {

    private AppConstant() {}
    public static final String API_VERSION = "/api/v1";
    public static final String API_BASE = API_VERSION + "/royal-settle";

    public static final String[] PUBLIC_URLS = {
            "/onboarding/login",
            "/onboarding/signup",
            "/onboarding/refresh-token",
            "/otp/**",
            API_VERSION + "/auth/**",
            API_VERSION + "/public/**",
            "/actuator/health",
            "/actuator/info",
            "/actuator/metrics"
    };

    public static final String[] ACTUATOR_URLS = {
            "/actuator/**"
    };
    public static final String[] ADMIN_URLS = {
            API_VERSION + "/admin/**"
    };

    public static final String[] USER_URLS = {
            API_VERSION + "/user/**"
    };
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final DateTimeFormatter CODE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
}
