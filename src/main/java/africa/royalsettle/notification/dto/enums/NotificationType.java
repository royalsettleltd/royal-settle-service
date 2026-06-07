package africa.royalsettle.notification.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Channel used to deliver a notification")
public enum NotificationType {
    SMS,
    EMAIL,
}
