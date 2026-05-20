package africa.royalsettle.notification.service;

import africa.royalsettle.notification.dto.request.NotificationRequest;

public interface EmailService {
    void send (NotificationRequest notificationRequest);
}
