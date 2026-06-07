package africa.royalsettle.notification.dto.request;

import africa.royalsettle.notification.dto.enums.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "OTP verification request")
public class OtpVerifyRequest {

    @NotNull(message = "notificationType is required")
    @Schema(description = "Channel through which the OTP was delivered", example = "EMAIL")
    private NotificationType notificationType;

    @Email(message = "emailAddress must be valid")
    @Schema(description = "Required when notificationType is EMAIL", example = "user@example.com")
    private String emailAddress;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phoneNumber must be 7 to 15 digits and may start with +")
    @Schema(description = "Required when notificationType is SMS", example = "+2348012345678")
    private String phoneNumber;

    @NotBlank(message = "otp is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "otp must be 6 digits")
    @Schema(description = "Six-digit one-time password", example = "123456", pattern = "^[0-9]{6}$")
    private String otp;

    public String recipient() {
        return notificationType == NotificationType.EMAIL ? emailAddress : phoneNumber;
    }
}
