package africa.royalsettle.notification.service;

import africa.royalsettle.notification.dto.NotificationRequest;

public interface SmsService {
    void sendSms(NotificationRequest notificationRequest);
}
