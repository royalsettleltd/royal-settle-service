package africa.royalsettle.common.dto;

import africa.royalsettle.common.enums.ResponseCode;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class BaseResponse implements Serializable {

    protected boolean requestSuccessful;

    protected String responseMessage;

    @JsonAlias("code")
    protected String responseCode;

    @JsonAlias("message")
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