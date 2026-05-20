package africa.royalsettle.notification.dto.request;

import africa.royalsettle.notification.dto.enums.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpVerifyRequest {

    @NotNull(message = "notificationType is required")
    private NotificationType notificationType;

    @Email(message = "emailAddress must be valid")
    private String emailAddress;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phoneNumber must be 7 to 15 digits and may start with +")
    private String phoneNumber;

    @NotBlank(message = "otp is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "otp must be 6 digits")
    private String otp;

    public String recipient() {
        return notificationType == NotificationType.EMAIL ? emailAddress : phoneNumber;
    }
}
