package africa.royalsettle.common.dto;

import lombok.Getter;

@Getter
public enum ResponseCode {
    // 100xx

    // 200xx
    OK("20000", "OK"),
    PROCESSING("200002", "The request is being processed, Please try again later."),
    SUCCESSFUL("200003", "Operation completed successfully"),

    // 400xx
    BAD_REQUEST(
            "400000",
            "The request could not be completed due to malformed syntax. Kindly crosscheck and try again."),

    // 401xxx
    UNAUTHORIZED(
            "401000",
            "The request could not be completed because it lacks valid authentication credentials."),

    FORBIDDEN("403000", "You do not have sufficient rights to access this service."),

    // 404xxx
    NOT_FOUND("404000", "The requested resource was not found in the system."),

    // 500xxx
    INTERNAL_SERVER_ERROR(
            "500000",
            "An unexpected error occurred while processing your request. Please try again later."),
    DATA_ACCESS_ERROR(
            "500001",
            "An unexpected error occurred while processing your request. Please try again later."),

    // 600xxx
    COSMOS_ACCESS_ERROR(
            "600000",
            "Unable to establish a connection with the authorization server. Please try again later."),

    // 700xxx
    PENDING_APPROVAL("700000", "This request is pending approval"),

    SERVER_ERROR(
            "500002",
            "Your Request could not be completed at this time. Please try again after sometime."),

    USER_TYPE_UNAUTHORIZED("403000", "User type not Authorised"),
    INVALID_AUTH_PIN("400038", "Invalid Transaction PIN");

    private String code;

    private String description;

    ResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
}