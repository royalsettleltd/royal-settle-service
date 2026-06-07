package africa.royalsettle.common.constants;

public final class OpenApiExamples {

    public static final String BAD_REQUEST = """
            {
              "requestSuccessful": false,
              "responseMessage": "The request could not be processed",
              "responseCode": "400000",
              "responseBody": null
            }
            """;

    public static final String UNAUTHORIZED = """
            {
              "requestSuccessful": false,
              "responseMessage": "Authentication is required",
              "responseCode": "401000",
              "responseBody": null
            }
            """;

    public static final String FORBIDDEN = """
            {
              "requestSuccessful": false,
              "responseMessage": "You do not have sufficient rights to access this service",
              "responseCode": "403000",
              "responseBody": null
            }
            """;

    public static final String NOT_FOUND = """
            {
              "requestSuccessful": false,
              "responseMessage": "The requested resource was not found",
              "responseCode": "404000",
              "responseBody": null
            }
            """;

    public static final String INTERNAL_SERVER_ERROR = """
            {
              "requestSuccessful": false,
              "responseMessage": "An unexpected error occurred",
              "responseCode": "500000",
              "responseBody": null
            }
            """;

    public static final String UNPROCESSABLE_ENTITY = """
            {
              "requestSuccessful": false,
              "responseMessage": "The requested operation is not supported",
              "responseCode": "422000",
              "responseBody": null
            }
            """;

    private OpenApiExamples() {
    }
}
