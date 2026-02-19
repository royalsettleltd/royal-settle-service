package africa.royalsettle.notification.service;

import africa.royalsettle.notification.dto.NotificationRequest;

public interface EmailService {
    void send (NotificationRequest notificationRequest);
}
