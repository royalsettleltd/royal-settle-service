package africa.royalsettle.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "OTP operation result")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpResponse {
    @Schema(example = "OTP sent successfully")
    private String message;
    @Schema(description = "Whether OTP verification succeeded", example = "false")
    private boolean verified;
    private String otp;
}
