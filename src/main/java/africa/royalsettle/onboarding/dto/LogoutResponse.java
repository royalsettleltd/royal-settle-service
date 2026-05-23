package africa.royalsettle.onboarding.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutResponse {
    private String message;
}
