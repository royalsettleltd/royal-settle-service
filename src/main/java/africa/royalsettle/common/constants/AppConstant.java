package africa.royalsettle.common.constants;

import java.time.format.DateTimeFormatter;

public class AppConstant {

    private AppConstant() {}
    public static final String[] PUBLIC_URLS = {
            "/auth/login",
            "/auth/refresh-token",
            "/otp/**",
            "/customer/create-account",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health",
            "/actuator/info"
    };

    public static final String[] ACTUATOR_URLS = {
            "/actuator/**"
    };
    public static final String[] ADMIN_URLS = {
            "/admin/**"
    };

    public static final String[] USER_URLS = {
            "/thrift/**"
    };
    public static final String ROLE_ADMIN = "ROYALSETTLE_ADMIN";
    public static final String ROLE_USER = "ROYALSETTLE_USER";
    public static final DateTimeFormatter CODE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
}
