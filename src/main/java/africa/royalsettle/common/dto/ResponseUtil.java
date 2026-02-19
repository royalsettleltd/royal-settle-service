package africa.royalsettle.common.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseUtil {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public BaseResponse buildErrorResponse(
            String code, String message, HttpServletRequest httpServletRequest) {
        return resolveMessageKeys(
                BaseResponse.builder()
                        .requestSuccessful(false)
                        .responseCode(code)
                        .responseMessage(stripPackageNames(message))
                        .build(),
                httpServletRequest);
    }

    public BaseResponse buildErrorResponse(
            ResponseCode responseCode, HttpServletRequest httpServletRequest) {
        return buildErrorResponse(
                responseCode.getCode(), responseCode.getDescription(), httpServletRequest);
    }

    public BaseResponse buildErrorResponse(String message, HttpServletRequest httpServletRequest) {
        return buildErrorResponse("01", message, httpServletRequest);
    }

    public BaseResponse buildErrorResponse(
            String message, Throwable e, HttpServletRequest httpServletRequest) {
        log.error(e.getMessage());
        return buildErrorResponse(message, httpServletRequest);
    }

    public BaseResponse buildErrorResponse(
            String code, String message, Throwable e, HttpServletRequest httpServletRequest) {
        log.error("Error Message: {}", e.getMessage(), e.getCause());

        if (code == null) {
            return buildErrorResponse(message, httpServletRequest);
        }
        return buildErrorResponse(code, message, httpServletRequest);
    }

    public BaseResponse buildErrorResponse(
            ResponseCode responseCode, Throwable e, HttpServletRequest httpServletRequest) {
        log.error("Error Message: {}", e.getMessage(), e.getCause());

        return buildErrorResponse(
                responseCode.getCode(), responseCode.getDescription(), httpServletRequest);
    }

    private BaseResponse resolveMessageKeys(BaseResponse baseResponse, HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return baseResponse;
        }

        final var locale = localeResolver.resolveLocale(request);
        baseResponse.setResponseMessage(
                resolveKey(baseResponse.getResponseMessage(), baseResponse.getArgs(), locale));
        return baseResponse;
    }

    public String resolveKey(String key, Object[] objects, Locale locale) {
        try {
            return messageSource.getMessage(key, objects, locale);
        } catch (Exception ignored) {
            return key;
        }
    }

    public static String stripPackageNames(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) return "";
        if (rawInput.indexOf(' ') < 0) return rawInput;
        Pattern packageNamePattern = Pattern.compile("(\\w+\\.\\w+\\.\\w+)");
        return packageNamePattern.matcher(rawInput).replaceAll("");
    }
}