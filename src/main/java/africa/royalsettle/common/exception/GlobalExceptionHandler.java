package africa.royalsettle.common.exception;

import africa.royalsettle.common.dto.BaseResponse;
import africa.royalsettle.common.dto.ResponseCode;
import africa.royalsettle.common.dto.ResponseUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static africa.royalsettle.common.dto.ResponseCode.BAD_REQUEST;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ResponseUtil responseUtil;

    protected static final String ACCESS_DENIED =
            "You are not authorised to perform this action. Kindly contact administrator to review your access.";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse handleNotFoundException(EntityNotFoundException e) {

        String errorMessage = e.getMessage();
        errorMessage =
                StringUtils.isBlank(errorMessage) ? ResponseCode.NOT_FOUND.getDescription() : errorMessage;

        return new BaseResponse(ResponseCode.NOT_FOUND.getCode(), errorMessage);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {

        var response = new BaseResponse(BAD_REQUEST);
        if (StringUtils.isNotBlank(e.getParameterName())) {
            response.setResponseMessage(String.format("%s is a required", e.getParameterName()));
        }
        return response;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = extractEnumErrorMessage(e);
        return new BaseResponse(BAD_REQUEST.getCode(), message);
    }

    private String extractEnumErrorMessage(HttpMessageNotReadableException e) {
        Throwable cause = e.getCause();

        if (!(cause instanceof InvalidFormatException invalidFormatException)) {
            return BAD_REQUEST.getDescription();
        }

        if (Objects.isNull(invalidFormatException.getTargetType()) || !invalidFormatException.getTargetType().isEnum()) {
            return BAD_REQUEST.getDescription();
        }

        String fieldName = getFieldName(invalidFormatException);
        String allowedValues = getAllowedEnumValues(invalidFormatException.getTargetType());

        return String.format("Invalid %s. Must be one of: %s", fieldName, allowedValues);
    }

    private String getFieldName(InvalidFormatException ex) {
        if (Objects.isNull(ex.getPath()) || ex.getPath().isEmpty()) {
            return "field";
        }
        return ex.getPath().get(ex.getPath().size() - 1).getFieldName();
    }

    @SuppressWarnings("unchecked")
    private String getAllowedEnumValues(Class<?> enumClass) {
        Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) enumClass;
        return java.util.Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .collect(java.util.stream.Collectors.joining(", "));
    }


    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public BaseResponse handleUnsupportedOperationException(UnsupportedOperationException e) {
        var response = new BaseResponse();
        response.setResponseCode(BAD_REQUEST.getCode());
        response.setResponseMessage(e.getMessage());
        response.setStatus(HttpStatus.BAD_REQUEST.toString());
        return response;
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseResponse handleDataAccessException(DataAccessException e) {
        log.error("DataAccessException: ", e);
        var response = new BaseResponse();
        response.setResponseCode(ResponseCode.DATA_ACCESS_ERROR.getCode());
        response.setResponseMessage(ResponseCode.DATA_ACCESS_ERROR.getDescription());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        return response;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse handleAccessDeniedException(
            Exception e, HttpServletRequest httpServletRequest) {
        return responseUtil.buildErrorResponse(ACCESS_DENIED, e, httpServletRequest);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse handleException(Exception e) {
        log.error("handling unexpected error: ", e);
        var response = new BaseResponse();
        response.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR.getCode());
        response.setResponseMessage(ResponseCode.INTERNAL_SERVER_ERROR.getDescription());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        return response;
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException: ", e);

        var response = new BaseResponse();
        response.setResponseCode(BAD_REQUEST.getCode());
        response.setResponseMessage(BAD_REQUEST.getDescription());
        response.setRequestSuccessful(false);
        response.setStatus(HttpStatus.BAD_REQUEST.toString());
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: ", e);

        var response = new BaseResponse();
        response.setResponseCode(BAD_REQUEST.getCode());
        response.setResponseMessage(BAD_REQUEST.getDescription());
        response.setRequestSuccessful(false);
        response.setStatus(HttpStatus.BAD_REQUEST.toString());
        return response;
    }

}
