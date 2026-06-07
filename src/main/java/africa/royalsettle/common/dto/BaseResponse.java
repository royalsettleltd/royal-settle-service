package africa.royalsettle.common.dto;

import africa.royalsettle.common.enums.ResponseCode;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Data
@Schema(description = "Standard Royal Settle API response envelope")
public class BaseResponse implements Serializable {

    @Schema(description = "Whether the request completed successfully")
    protected boolean requestSuccessful;

    @Schema(description = "Human-readable response message")
    protected String responseMessage;

    @JsonAlias("code")
    @Schema(description = "Application response code")
    protected String responseCode;

    @JsonAlias("message")
    @Schema(description = "Endpoint-specific response payload", nullable = true)
    private Object responseBody;

    @JsonIgnore
    private String[] args;

    @JsonIgnore
    private String status;

    public BaseResponse(ResponseCode responseCode) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(responseCode.getDescription());
    }

    public BaseResponse(String code, String description) {
        this.setResponseCode(code);
        this.setResponseMessage(description);
    }

    public BaseResponse(String code, String description, String version) {
        this(code, description);
    }

    public BaseResponse(ResponseCode responseCode, String description) {
        this.setResponseCode(responseCode.getCode());
        this.setResponseMessage(description);
    }

    public BaseResponse(
            ResponseCode responseCode, String description, String version) {
        this(responseCode, description);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
        }
        return "";
    }
}
