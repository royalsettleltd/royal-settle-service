package africa.royalsettle.notification.dto.request;

import africa.royalsettle.notification.dto.enums.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "OTP delivery request. Supply the recipient matching notificationType.")
public class OtpSendRequest {

    @NotNull(message = "notificationType is required")
    @Schema(description = "Delivery channel", example = "EMAIL")
    private NotificationType notificationType;

    @Email(message = "emailAddress must be valid")
    @Schema(description = "Required when notificationType is EMAIL", example = "user@example.com")
    private String emailAddress;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phoneNumber must be 7 to 15 digits and may start with +")
    @Schema(description = "Required when notificationType is SMS", example = "+2348012345678")
    private String phoneNumber;

    public String recipient() {
        return notificationType == NotificationType.EMAIL ? emailAddress : phoneNumber;
    }
}
