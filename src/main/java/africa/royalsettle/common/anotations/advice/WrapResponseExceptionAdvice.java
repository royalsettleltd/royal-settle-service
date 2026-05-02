package africa.royalsettle.common.anotations.advice;

import africa.royalsettle.common.anotations.IgnoreWrapResponse;
import africa.royalsettle.common.anotations.WrapResponse;
import africa.royalsettle.common.dto.BaseResponse;
import africa.royalsettle.common.enums.ResponseCode;
import africa.royalsettle.common.dto.ResponseUtil;
import africa.royalsettle.common.exception.GlobalExceptionHandler;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

@Slf4j
@ControllerAdvice(annotations = WrapResponse.class)

public class WrapResponseExceptionAdvice extends GlobalExceptionHandler
        implements ResponseBodyAdvice<Object> {

    public WrapResponseExceptionAdvice(ResponseUtil responseUtil) {
        super(responseUtil);
    }

    @Override
    public boolean supports(
            MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        var method = methodParameter.getMethod();
        if (Objects.isNull(method)) return true;

        return method.getDeclaredAnnotation(IgnoreWrapResponse.class) == null;
    }

    @Override
    public @NonNull Object beforeBodyWrite(
            @Nullable Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {

        BaseResponse res = new BaseResponse();
        res.setResponseCode(ResponseCode.OK.getCode());
        res.setResponseMessage(ResponseCode.OK.getDescription());
        res.setRequestSuccessful(true);
        res.setResponseBody(body);
        return res;
    }
}
