package africa.royalsettle.notification.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpResponse {
    private String message;
    private boolean verified;
}
