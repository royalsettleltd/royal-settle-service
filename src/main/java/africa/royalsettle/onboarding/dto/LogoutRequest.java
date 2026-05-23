package africa.royalsettle.onboarding.dto;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
