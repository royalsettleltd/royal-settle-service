package africa.royalsettle.notification.service;

import africa.royalsettle.notification.dto.request.NotificationRequest;

public interface SmsService {
    void sendSms(NotificationRequest notificationRequest);
}
