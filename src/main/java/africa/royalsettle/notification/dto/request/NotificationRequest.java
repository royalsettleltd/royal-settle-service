package africa.royalsettle.notification.dto.request;

import africa.royalsettle.notification.dto.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NotificationRequest {

    private NotificationType notificationType;
    private String sender;
    private String recipient;
    private String message;
    private String subject;
    private String type;
    private String amount;
    private String code;
    private List<NotificationAttachment> attachments;
}
